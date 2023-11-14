package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.validation.FormValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectFieldControl extends FieldControl<ObjectFieldControl, ReadOnlyListProperty<Pair<String, Property<?>>>, ObservableList<Pair<String, Property<?>>>> {
    private final EditorFormPane formPane;

    public ObjectFieldControl(EditorFormPane editorFieldPane) {
        super();
        this.formPane = editorFieldPane;
    }

    @Override
    protected void bindFields() {
        super.bindFields();
        this.changedProperty().bind(this.formPane.changedProperty());
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
        return this.formPane;
    }

    @Override
    public boolean fulfillsRequired(ObservableList<Pair<String, Property<?>>> value) {
        return value != null && !value.isEmpty();
    }

    @Override
    protected void checkValid(ObservableList<Pair<String, Property<?>>> newValue) {
        super.checkValid(newValue);
        this.formPane.validate();
        this.formPane.messagesProperty().forEach(message -> message.fieldProperty()
                .set(this.getLabelString())
        );
        this.messagesProperty()
                .addAll(this.formPane.messagesProperty());
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
        this.formPane.persist();
        super.persist();
    }

    @Override
    public void reset() {
        this.formPane.reset();
        super.reset();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Either<ObservableList<Pair<String, Property<?>>>, ValidationResult> castObject(Object value) {
        if (value instanceof ObservableList<?> list) {
            return Either.left((ObservableList<Pair<String, Property<?>>>) list);
        }
        return Either.right(ValidationResult.error("Value not a list"));
    }

    public static ObjectFieldControl of(ReferencedObjectFileFieldDefinition definition) throws FormException {
        ObjectType objectType = Registries.getObjectTypes()
                .getValue(definition.objectId());

        if (objectType != null) {
            return new ObjectFieldControl(EditorFormPane.of(
                    objectType.fileForm(),
                    Registries.getSerializerTypes()
                            .supplyList(objectType),
                    null
            ));
        } else {
            throw new FormException("No Object with Id: %s".formatted(definition.objectId()));
        }
    }
}
