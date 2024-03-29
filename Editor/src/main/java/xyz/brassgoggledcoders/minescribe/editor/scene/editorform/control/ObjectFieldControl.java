package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.StringObjectTransform;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.validation.FormValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldMessagesEvent;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.SerializerEditorFieldPane;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectFieldControl extends FieldControl<ObjectFieldControl, ReadOnlyListProperty<Pair<String, Property<?>>>, ObservableList<Pair<String, Property<?>>>> {
    private final TitledPane titledPane;
    private final EditorFormPane formPane;
    private final StringObjectTransform transform;

    public ObjectFieldControl(EditorFormPane editorFieldPane, StringObjectTransform transform) {
        super();
        this.transform = transform;
        this.titledPane = new TitledPane();
        this.titledPane.getStyleClass()
                .add("paned-field");
        this.titledPane.textProperty()
                .bind(this.getFieldInfo()
                        .name()
                );
        this.formPane = editorFieldPane;
        this.formPane.setPadding(Insets.EMPTY);
        this.titledPane.setContent(this.formPane);
        this.titledPane.addEventHandler(FieldMessagesEvent.EVENT_TYPE, this::handleFieldMessagesEvent);
    }

    public void handleFieldMessagesEvent(FieldMessagesEvent fieldMessagesEvent) {
        this.validate();
    }

    @Override
    protected void bindFields() {
        super.bindFields();
        this.changedProperty().bind(this.formPane.changedProperty());
        this.requiredProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.titledPane.setExpanded(newValue);
            }
        }));
    }

    @Override
    protected JsonElement saveControl() {
        return this.formPane.persistedObjectProperty()
                .get();
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonObject()) {
            this.formPane.setPersistedObject(jsonElement.getAsJsonObject());
            this.titledPane.setExpanded(true);
        } else if (jsonElement != null && jsonElement.isJsonPrimitive() && transform != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(transform.field(), jsonElement.getAsString());
            if (transform.type().isPresent()) {
                this.formPane.getEditorFieldPanes()
                        .filter(editorFieldPane -> {
                            if (editorFieldPane instanceof SerializerEditorFieldPane serializerEditorFieldPane) {
                                return serializerEditorFieldPane.getFileForm() == this.formPane.primaryFormProperty().get();
                            }

                            return false;
                        })
                        .findFirst()
                        .ifPresent(editorFieldPane -> jsonObject.addProperty(
                                editorFieldPane.getFieldName(),
                                transform.type()
                                        .get()
                                        .toString()
                        ));
            }
            this.formPane.setPersistedObject(jsonObject);
            this.titledPane.setExpanded(true);
        } else {
            this.formPane.setPersistedObject(new JsonObject());
        }
    }

    @Override
    public ReadOnlyListProperty<Pair<String, Property<?>>> valueProperty() {
        return this.formPane.formValuesProperty();
    }

    @Override
    public Node getNode() {
        return this.titledPane;
    }

    @Override
    public boolean fulfillsRequired(ObservableList<Pair<String, Property<?>>> value) {
        return value != null && !value.isEmpty();
    }

    @Override
    protected Set<FieldMessage> additionalChecks(ObservableList<Pair<String, Property<?>>> newValue) {
        this.formPane.validate();
        return this.formPane.formMessagesProperty()
                .stream()
                .map(message -> new FieldMessage(
                        this.getFieldInfo(),
                        message.type(),
                        message.message()
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public ObjectFieldControl withValidations(List<Validation<?>> validations) {
        List<FormValidation> formValidations = validations.stream()
                .filter(FormValidation.class::isInstance)
                .map(FormValidation.class::cast)
                .toList();
        if (!formValidations.isEmpty()) {
            this.formPane.formValidationsProperty()
                    .addAll(formValidations);
        }
        return super.withValidations(validations);
    }

    @Override
    public void persist() {
        if (this.containsUserData()) {
            this.formPane.persist();
        }
        super.persist();
    }

    @Override
    public void reset() {
        this.formPane.reset();
        super.reset();
    }

    @Override
    public void validateAll() {
        super.validateAll();
        if (this.requiredProperty().get() || this.containsUserData()) {
            this.formPane.validateAll();
        }
    }

    @Override
    public boolean containsUserData() {
        return this.formPane.getEditorFieldPanes()
                .anyMatch(editorFieldPane -> {
                    if (editorFieldPane.getFieldContent() instanceof IValueContent<?, ?, ?> valueContent) {
                        return valueContent.containsUserData();
                    }

                    return false;
                });
    }

    public static ObjectFieldControl of(ReferencedObjectFileFieldDefinition definition) throws FormException {
        ObjectType objectType = EditorRegistries.getObjectTypes()
                .getValue(definition.objectId());

        if (objectType != null) {
            EditorFormPane editorFormPane = EditorFormPane.of(
                    objectType.fileForm(),
                    List.of(objectType),
                    null
            );
            return new ObjectFieldControl(editorFormPane, definition.transform().orElse(null));
        } else {
            throw new FormException("No Object with Id: %s".formatted(definition.objectId()));
        }
    }
}
