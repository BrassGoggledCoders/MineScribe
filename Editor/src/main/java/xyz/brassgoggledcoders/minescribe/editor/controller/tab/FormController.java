package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageHandler;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.message.MineScribeMessage;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class FormController implements IFileEditorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @FXML
    public VBox formPane;

    @FXML
    public AnchorPane saveButtonPane;
    @FXML
    public Button saveButton;
    @FXML
    public Button resetButton;

    public Tooltip validationToolTip;
    public Tab tab;

    private EditorFormPane editorForm;

    private Path filePath;
    private final BooleanProperty fileSaved;

    public FormController() {
        this.fileSaved = new SimpleBooleanProperty(false);
    }

    public void setFormInfo(Path filePath, PackContentParentType parentType, @Nullable PackContentChildType childType) {
        FileForm fileForm = parentType.getForm()
                .or(() -> Optional.ofNullable(childType)
                        .flatMap(PackContentType::getForm)
                )
                .orElseThrow();

        JsonObject persistableObject = null;

        this.filePath = filePath;
        if (Files.exists(this.filePath)) {
            try {
                String jsonString = Files.readString(this.filePath);
                JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                if (jsonElement.isJsonObject()) {
                    persistableObject = jsonElement.getAsJsonObject();
                }
                this.fileSaved.set(true);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to load File for %s".formatted(this.filePath));
            }
        }

        try {
            this.editorForm = EditorFormPane.of(
                    fileForm,
                    Registries.getSerializerTypes()
                            .supplyList(parentType, childType),
                    persistableObject
            );
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
                                FileHandler.getInstance().reloadClosestNode(filePath);
                            } catch (IOException e) {
                                LOGGER.error("Failed to write file {}", this.filePath, e);
                            }
                        }
                    });

            validationToolTip = new Tooltip();

            validationToolTip.textProperty().bind(this.editorForm.errorMessagesProperty()
                    .map(errorSet -> errorSet.stream()
                            .reduce((stringA, stringB) -> stringA + System.lineSeparator() + stringB)
                            .orElse("")
                    )
            );

            this.resetButton.disableProperty()
                    .bind(this.editorForm.changedProperty()
                            .not()
                    );
            this.saveButton.disableProperty()
                    .bind(Bindings.and(
                            this.editorForm.changedProperty(),
                            this.editorForm.validProperty()
                    ).not());
            this.editorForm.errorMessagesProperty()
                    .addListener((SetChangeListener<String>) change -> {
                        if (change.getSet().isEmpty()) {
                            Tooltip.uninstall(saveButtonPane, validationToolTip);
                        } else {
                            Tooltip.install(saveButtonPane, validationToolTip);
                        }
                    });
            this.formPane.getChildren()
                    .add(this.editorForm);

            if (!this.fileSaved.get()) {
                MineScribeMessage notSavedMessage = new MineScribeMessage(
                        MessageType.WARNING,
                        this.filePath,
                        null,
                        "File is not saved"
                );

                notSavedMessage.validProperty()
                        .bind(this.fileSaved.not());

                MessageHandler.getInstance()
                        .addMessage(notSavedMessage);
            }
        } catch (FormException e) {
            LOGGER.error("Failed to Load Form", e);
            e.showErrorDialog();
        }
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

    @Override
    public @Nullable Path getPath() {
        return this.filePath;
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
        }
    }
}
