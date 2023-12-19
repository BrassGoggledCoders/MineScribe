package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.IFullName;
import xyz.brassgoggledcoders.minescribe.core.validation.FormValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldAddedEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldMessagesEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldRemovedEvent;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EditorFormPane extends GridPane {
    private static final ColumnConstraints LABEL = Suppliers.memoize(() -> {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(50);
        columnConstraints.setHgrow(Priority.SOMETIMES);
        return columnConstraints;
    }).get();
    private static final ColumnConstraints CONTENT = Suppliers.memoize(() -> {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        return columnConstraints;
    }).get();

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorFormPane.class);
    private final ReadOnlyObjectProperty<FileForm> primaryForm;
    private final ObjectProperty<FileForm> serializerForm;
    private final ObjectProperty<JsonObject> persistedObject;
    private final SimpleListProperty<Pair<String, Property<?>>> formValues;
    private final ListProperty<FormValidation> formValidations;
    private final SetProperty<FieldMessage> formMessages;
    private final SetProperty<FieldMessage> fieldMessages;

    private final BooleanProperty valid;
    private final BooleanProperty changed;
    private final BooleanProperty persistable;

    public EditorFormPane(FileForm primaryForm, List<EditorFieldPane<?>> fieldPanes, List<FormValidation> formValidations, @Nullable JsonObject persistedObject) {
        this.primaryForm = new SimpleObjectProperty<>(primaryForm);
        this.serializerForm = new SimpleObjectProperty<>();
        this.persistedObject = new SimpleObjectProperty<>(persistedObject);
        this.valid = new SimpleBooleanProperty(true);
        this.changed = new SimpleBooleanProperty(false);
        this.persistable = new SimpleBooleanProperty(false);
        this.formValues = new SimpleListProperty<>(FXCollections.observableArrayList(pair -> new Observable[]{pair.getSecond()}));
        this.formValues.addListener((ListChangeListener<Pair<String, Property<?>>>) c -> validate());

        this.serializerForm.addListener((observable, oldValue, newValue) -> reloadSerializerForm(oldValue, newValue));

        this.formValidations = new SimpleListProperty<>(FXCollections.observableArrayList(formValidations));
        this.formValidations.addListener((ListChangeListener<FormValidation>) c -> validate());

        this.formMessages = new SimpleSetProperty<>(FXCollections.observableSet());
        this.fieldMessages = new SimpleSetProperty<>(FXCollections.observableSet());

        this.addEventHandler(FieldMessagesEvent.EVENT_TYPE, this::handleFieldMessagesEvent);

        this.getColumnConstraints().setAll(LABEL, CONTENT);
        this.setHgap(5);

        this.getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    List<Pair<String, Property<?>>> newProperties = new ArrayList<>();
                    for (Node addedChild : c.getAddedSubList()) {
                        if (addedChild instanceof EditorFieldPane<?> editorFieldPane) {
                            editorFieldPane.fireEvent(new FieldAddedEvent(editorFieldPane.getContent()
                                    .getFieldInfo()
                            ));
                            if (editorFieldPane.getContent() instanceof IValueContent<?, ?, ?> valueContent) {
                                newProperties.add(Pair.of(
                                        editorFieldPane.getFieldName(),
                                        (Property<?>) valueContent.valueProperty()
                                ));
                            }
                        }
                    }
                    this.formValues.addAll(newProperties);
                }
                if (c.wasRemoved()) {
                    List<String> fieldsRemoved = new ArrayList<>();
                    for (Node removedChild : c.getRemoved()) {
                        if (removedChild instanceof EditorFieldPane<?> editorFieldPane) {
                            editorFieldPane.fireEvent(new FieldRemovedEvent(editorFieldPane.getFieldContent()
                                    .getFieldInfo()
                            ));
                            fieldsRemoved.add(editorFieldPane.getFieldName());
                        }
                    }
                    this.formValues.removeIf(pair -> fieldsRemoved.contains(pair.getFirst()));
                }
            }
        });
        fieldPanes.forEach(this::addFieldPanel);

        //validate();
        updateChangedProperty();

        this.setPadding(new Insets(10));
    }

    public void setPersistedObject(@NotNull JsonObject jsonObject) {
        this.persistedObject.set(jsonObject);
        List<SerializerEditorFieldPane> serializerEditorFieldPanes = this.getEditorFieldPanes()
                .filter(SerializerEditorFieldPane.class::isInstance)
                .map(SerializerEditorFieldPane.class::cast)
                .toList();

        //These can alter the list
        for (SerializerEditorFieldPane serializerEditorFieldPane : serializerEditorFieldPanes) {
            serializerEditorFieldPane.setValue(jsonObject.get(serializerEditorFieldPane.getFieldName()));
        }

        for (EditorFieldPane<?> editorFieldPane : this.getEditorFieldPanes().toList()) {
            setField(editorFieldPane, jsonObject);
        }
    }

    @SuppressWarnings("unused")
    public ReadOnlyObjectProperty<FileForm> primaryFormProperty() {
        return this.primaryForm;
    }

    public ObjectProperty<FileForm> serializerFormProperty() {
        return this.serializerForm;
    }

    public ReadOnlyObjectProperty<JsonObject> persistedObjectProperty() {
        return this.persistedObject;
    }

    @SuppressWarnings("unused")
    public ReadOnlyBooleanProperty changedProperty() {
        return this.changed;
    }

    @SuppressWarnings("unused")
    public ReadOnlyBooleanProperty validProperty() {
        return this.valid;
    }

    public ReadOnlyListProperty<Pair<String, Property<?>>> formValuesProperty() {
        return this.formValues;
    }

    public ListProperty<FormValidation> formValidationsProperty() {
        return this.formValidations;
    }

    public void validate() {
        Set<FieldMessage> newErrorMessages = this.formValidations.stream()
                .map(formValidation -> formValidation.validate(this.formValues.stream()
                        .filter(pair -> pair.getSecond().getValue() != null)
                        .collect(Collectors.toMap(Pair::getFirst, pair -> pair.getSecond().getValue()))
                ))
                .filter(Predicate.not(ValidationResult::isValid))
                .map(validationResult -> new FieldMessage(
                        null,
                        MessageType.ERROR,
                        validationResult.getMessage()
                ))
                .collect(Collectors.toSet());


        this.formMessages.setValue(FXCollections.observableSet(newErrorMessages));

        boolean noErrorMessages = this.formMessages.isEmpty() && this.fieldMessages.isEmpty();
        boolean fieldsValid = this.getEditorFieldPanes()
                .allMatch(EditorFieldPane::isValid);
        valid.setValue(noErrorMessages && fieldsValid);
        updatePersistableProperty();
    }

    public void validateAll() {
        this.validate();
        this.getEditorFieldPanes()
                .forEach(editorFieldPane -> {
                    if (editorFieldPane.getFieldContent() instanceof IValueContent<?, ?, ?> valueContent) {
                        valueContent.validateAll();
                    }
                });
    }

    public void handleFieldMessagesEvent(FieldMessagesEvent fieldMessagesEvent) {
        this.fieldMessages.addAll(fieldMessagesEvent.getAddedMessages());
        this.fieldMessages.removeAll(fieldMessagesEvent.getRemovedMessages());
        this.validate();
    }

    public void reset() {
        this.getEditorFieldPanes()
                .forEach(EditorFieldPane::reset);
    }

    public void persist() {
        this.validateAll();
        if (this.valid.get()) {
            JsonObject newPersisted = new JsonObject();
            this.getEditorFieldPanes().forEach(editorFieldPane -> {
                editorFieldPane.persist();
                JsonElement jsonElement = editorFieldPane.getValue();
                if (jsonElement != null && !jsonElement.isJsonNull()) {
                    newPersisted.add(editorFieldPane.getFieldName(), jsonElement);
                }
            });

            if (!newPersisted.isEmpty()) {
                this.persistedObject.set(newPersisted);
            }
        }
    }

    //Intellij complains otherwise?
    @SuppressWarnings("RedundantTypeArguments")
    public Stream<EditorFieldPane<?>> getEditorFieldPanes() {
        return this.getChildren()
                .stream()
                .filter(EditorFieldPane.class::isInstance)
                .<EditorFieldPane<?>>map(EditorFieldPane.class::cast);
    }

    private void addFieldPanel(EditorFieldPane<?> newField) {
        newField.setFormPane(this);
        newField.validProperty()
                .addListener((observable, oldValue, newValue) -> validate());
        newField.changedProperty()
                .addListener((observable, oldValue, newValue) -> updateChangedProperty());

        if (this.persistedObjectProperty().get() != null) {
            JsonObject currentObject = this.persistedObjectProperty().get();
            setField(newField, currentObject);
        }

        List<EditorFieldPane<?>> editorFieldPanes = this.getEditorFieldPanes()
                .collect(Collectors.toList());

        if (editorFieldPanes.contains(newField)) {
            LOGGER.error("Editor Form already contains: " + newField);
        } else {
            editorFieldPanes.add(newField);
            editorFieldPanes.sort(this::sortChildren);

            this.getChildren().clear();

            boolean labelsOnSide = editorFieldPanes.stream()
                    .anyMatch(editorFieldPane -> !(editorFieldPane.getFieldContent().getNode() instanceof TitledPane));

            int currentRow = 0;
            for (EditorFieldPane<?> editorFieldPane : editorFieldPanes) {
                if (labelsOnSide) {
                    Label label = editorFieldPane.labelProperty().get();
                    if (label != null) {
                        this.add(label, 0, currentRow);
                        Tooltip tooltip = new Tooltip();
                        tooltip.textProperty().bind(label.textProperty());
                        Tooltip.install(label, tooltip);
                        label.setAlignment(Pos.CENTER_LEFT);
                    }

                    this.add(editorFieldPane, 1, currentRow++);
                } else {
                    this.add(editorFieldPane, 0, currentRow++, 2, 1);
                }

            }
        }

    }

    private int sortChildren(EditorFieldPane<?> editorFieldPaneOne, EditorFieldPane<?> editorFieldPaneTwo) {
        int compared;
        boolean paneOneMain = editorFieldPaneOne.getFileForm() == this.primaryForm.get();
        boolean paneTwoMain = editorFieldPaneTwo.getFileForm() == this.primaryForm.get();

        compared = -Boolean.compare(paneOneMain, paneTwoMain);

        if (compared == 0) {
            compared = Integer.compare(editorFieldPaneOne.getSortOrder(), editorFieldPaneTwo.getSortOrder());
        }
        return compared;
    }

    public ReadOnlySetProperty<FieldMessage> formMessagesProperty() {
        return this.formMessages;
    }

    public ReadOnlySetProperty<FieldMessage> fieldMessagesProperty() {
        return this.fieldMessages;
    }

    private void updateChangedProperty() {
        changed.setValue(this.getEditorFieldPanes()
                .anyMatch(EditorFieldPane::isChanged)
        );
        updatePersistableProperty();
    }

    private void updatePersistableProperty() {
        boolean canPersist = this.getEditorFieldPanes()
                .anyMatch(EditorFieldPane::isChanged);
        if (canPersist) {
            canPersist = this.getEditorFieldPanes()
                    .allMatch(EditorFieldPane::isValid);
        }
        persistable.set(canPersist);
    }

    private void reloadSerializerForm(FileForm oldValue, FileForm newValue) {
        if (oldValue != null) {
            this.formValidations.removeIf(oldValue.getValidations()::contains);
        }

        int[] rowsToRemove = this.getChildren()
                .stream()
                .flatMapToInt(child -> {
                    if (child instanceof EditorFieldPane<?> editorFieldPane) {
                        if (editorFieldPane.getFileForm() == oldValue) {
                            Integer index = GridPane.getRowIndex(editorFieldPane);
                            if (index != null) {
                                return IntStream.of(index);
                            }
                        }
                    }
                    return IntStream.empty();
                })
                .distinct()
                .toArray();

        if (rowsToRemove.length > 0) {
            this.getChildren()
                    .removeIf(node -> {
                        Integer integer = GridPane.getRowIndex(node);
                        if (integer != null) {
                            return IntStream.of(rowsToRemove)
                                    .anyMatch(integer::equals);
                        } else {
                            return false;
                        }
                    });
        }

        if (newValue != null) {
            for (FileField<?> field : newValue.getFields()) {
                try {
                    this.addFieldPanel(EditorFileFieldPane.of(newValue, field));
                } catch (FormException e) {
                    LOGGER.error(e.getMessage(), e);
                    e.showErrorDialog();
                }
            }
            this.formValidations.addAll(newValue.getValidations()
                    .stream()
                    .filter(FormValidation.class::isInstance)
                    .map(FormValidation.class::cast)
                    .collect(Collectors.toSet())
            );
        }
        validate();
        updateChangedProperty();
    }

    private void setField(EditorFieldPane<?> editorFieldPane, JsonObject jsonObject) {
        if (jsonObject.has(editorFieldPane.getFieldName())) {
            editorFieldPane.setValue(jsonObject.get(editorFieldPane.getFieldName()));
        }
    }

    public static EditorFormPane of(FileForm form, List<IFullName> parents,
                                    @Nullable JsonObject persistedObject) throws FormException {
        List<EditorFieldPane<?>> editorFieldPanes = new LinkedList<>();

        for (FileField<?> field : form.getFields()) {
            editorFieldPanes.add(EditorFileFieldPane.of(form, field));
        }

        SerializerEditorFieldPane.of(form, parents)
                .ifPresent(editorFieldPanes::add);

        List<FormValidation> filteredValidation = form.getValidations()
                .stream()
                .filter(FormValidation.class::isInstance)
                .map(FormValidation.class::cast)
                .toList();

        return new EditorFormPane(form, editorFieldPanes, filteredValidation, persistedObject);
    }
}
