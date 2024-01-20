package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.IFullName;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldMessagesEvent;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageHandler;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.message.MineScribeMessage;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.ExceptionDialog;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.UnsavedFileEditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.EditorFormTab;
import xyz.brassgoggledcoders.minescribe.editor.service.editoritem.IEditorItemService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

public class FormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final IEditorItemService editorItemService;
    private final Provider<Project> projectProvider;
    @FXML
    public VBox formPane;

    @FXML
    public AnchorPane saveButtonPane;
    @FXML
    public Button saveButton;
    @FXML
    public Button resetButton;

    public Tooltip validationToolTip;
    public EditorFormTab tab;

    private EditorFormPane editorForm;
    private final BooleanProperty fileSaved;

    @Inject
    public FormController(IEditorItemService editorItemService, Provider<Project> projectProvider) {
        this.editorItemService = editorItemService;
        this.projectProvider = projectProvider;
        this.fileSaved = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        this.formPane.addEventHandler(FieldMessagesEvent.EVENT_TYPE, this::handleMessages);
        this.tab.pathProperty()
                .addListener(this::handleInvalidate);
        this.tab.fileFormProperty()
                .addListener(this::handleInvalidate);
        this.tab.parentsProperty()
                .addListener(this::handleInvalidate);
    }

    public void handleInvalidate(Observable ignored) {
        this.reloadForm();
    }

    public void reloadForm() {
        FileForm fileForm = this.tab.fileFormProperty()
                .getValue();

        Path filePath = this.tab.pathProperty()
                .getValue();

        List<IFullName> parents = this.tab.parentsProperty();

        this.formPane.getChildren()
                .clear();
        if (fileForm != null && filePath != null && !parents.isEmpty()) {
            JsonObject persistableObject = null;

            if (Files.exists(filePath)) {
                try {
                    String jsonString = Files.readString(filePath);
                    JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                    if (jsonElement.isJsonObject()) {
                        persistableObject = jsonElement.getAsJsonObject();
                    }
                    this.fileSaved.set(true);
                } catch (IOException e) {
                    LOGGER.error("Failed to load File for {}", filePath, e);
                    ExceptionDialog.showDialog(
                            "Failed to load File for %s".formatted(filePath),
                            e
                    );
                }
            }

            try {
                this.editorForm = EditorFormPane.of(
                        fileForm,
                        parents,
                        null
                );

                validationToolTip = new Tooltip();

                validationToolTip.textProperty().bind(Bindings.concat(
                        this.editorForm.formMessagesProperty()
                                .map(errorSet -> errorSet.stream()
                                        .map(FieldMessage::message)
                                        .filter(Objects::nonNull)
                                        .reduce("Form Errors: ", (messageA, messageB) -> messageA + System.lineSeparator() + " * " + messageB)
                                ),
                        System.lineSeparator(),
                        this.editorForm.fieldMessagesProperty()
                                .map(errorSet -> errorSet.stream()
                                        .map(FieldMessage::message)
                                        .filter(Objects::nonNull)
                                        .reduce("Field Errors: ", (messageA, messageB) -> messageA + System.lineSeparator() + " * " + messageB)
                                )
                ));

                this.resetButton.disableProperty()
                        .bind(this.editorForm.changedProperty()
                                .not()
                        );
                this.saveButton.disableProperty()
                        .bind(Bindings.and(
                                this.editorForm.changedProperty(),
                                this.editorForm.validProperty()
                        ).not());

                this.formPane.getChildren()
                        .add(this.editorForm);

                if (persistableObject != null) {
                    this.editorForm.setPersistedObject(persistableObject);
                }

                this.editorForm.persistedObjectProperty()
                        .addListener((observable, oldValue, newValue) -> {
                            if (newValue != null && !newValue.isEmpty()) {
                                try {
                                    Files.createDirectories(filePath.getParent());
                                    Files.writeString(
                                            filePath,
                                            GSON.toJson(newValue),
                                            StandardCharsets.UTF_8,
                                            StandardOpenOption.WRITE,
                                            StandardOpenOption.CREATE,
                                            StandardOpenOption.TRUNCATE_EXISTING
                                    );
                                    this.fileSaved.set(true);
                                    this.editorItemService.reloadClosestNode(filePath);
                                } catch (IOException e) {
                                    LOGGER.error("Failed to write file {}", filePath, e);
                                    ExceptionDialog.showDialog(
                                            "Failed to write file %s".formatted(filePath),
                                            e
                                    );
                                }
                            }
                        });

                if (!this.fileSaved.get()) {
                    try {
                        Path parentDirectory = filePath.getParent();
                        Files.createDirectories(parentDirectory);
                        this.editorItemService.reloadClosestNode(parentDirectory);
                        TreeItem<EditorItem> closestNode = this.editorItemService.getClosestNode(parentDirectory, true);
                        if (closestNode != null) {
                            EditorItem parentEditorItem = closestNode.getValue();
                            if (parentEditorItem != null && parentEditorItem.getPath().equals(parentDirectory)) {
                                closestNode.getChildren()
                                        .add(new TreeItem<>(new UnsavedFileEditorItem(
                                                filePath.getFileName()
                                                        .toString(),
                                                filePath,
                                                this.projectProvider::get
                                        )));
                            }
                        }

                    } catch (IOException e) {
                        LOGGER.error("Failed to create unsaved editor item in file tree", e);
                        ExceptionDialog.showDialog("Failed to create unsaved editor item in file tree", e);
                    }

                    MineScribeMessage notSavedMessage = new MineScribeMessage(
                            MessageType.WARNING,
                            filePath,
                            null,
                            "File is not saved",
                            this.getPath()
                    );

                    notSavedMessage.validProperty()
                            .bind(this.fileSaved.not());

                    MessageHandler.getInstance()
                            .addMessage(notSavedMessage);
                }
            } catch (FormException e) {
                e.showErrorDialog();
            }
        }
    }

    private void handleMessages(FieldMessagesEvent fieldMessagesEvent) {
        for (FieldMessage fieldMessage : fieldMessagesEvent.getAddedMessages()) {
            MessageHandler.getInstance()
                    .addMessage(getMineScribeMessage(fieldMessage));
        }
        for (FieldMessage fieldMessage : fieldMessagesEvent.getRemovedMessages()) {
            MessageHandler.getInstance()
                    .removeByContext(fieldMessage);
        }
        if (this.editorForm.fieldMessagesProperty().isEmpty() && this.editorForm.formMessagesProperty().isEmpty()) {
            Tooltip.uninstall(saveButtonPane, validationToolTip);
        } else {
            Tooltip.install(saveButtonPane, validationToolTip);
        }
    }

    @NotNull
    private MineScribeMessage getMineScribeMessage(FieldMessage newMessage) {
        return new MineScribeMessage(
                newMessage.type(),
                this.getPath(),
                newMessage.fieldInfo()
                        .name()
                        .getValue(),
                newMessage.message(),
                newMessage
        );
    }

    public void saveForm(MouseEvent ignoredMouseEvent) {
        if (this.editorForm != null) {
            this.editorForm.persist();
        }
    }

    public void resetForm(MouseEvent ignoredMouseEvent) {
        if (this.editorForm != null) {
            this.editorForm.reset();
        }
    }

    public @Nullable Path getPath() {
        return this.tab.pathProperty()
                .getValue();
    }

    @FXML
    public void onCloseRequest(Event event) {
        if (!this.fileSaved.get()) {
            boolean allowClose = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close this file? It is unsaved.", ButtonType.YES, ButtonType.NO)
                    .showAndWait()
                    .map(buttonType -> buttonType == ButtonType.YES)
                    .orElse(false);

            if (!allowClose) {
                event.consume();
            }
        }
    }

    @FXML
    public void onClosed() {
        if (this.getPath() != null) {
            MessageHandler.getInstance()
                    .removeByPath(this.getPath());
            if (!this.fileSaved.get()) {
                TreeItem<EditorItem> parentTreeItem = this.editorItemService.getClosestNode(this.getPath().getParent(), false);

                if (parentTreeItem != null) {
                    parentTreeItem.getChildren()
                            .removeIf(childItem -> childItem.getValue() != null &&
                                    !childItem.getValue().isAutomatic() &&
                                    childItem.getValue().getPath().equals(this.getPath())
                            );
                }
            }
        }
    }
}
