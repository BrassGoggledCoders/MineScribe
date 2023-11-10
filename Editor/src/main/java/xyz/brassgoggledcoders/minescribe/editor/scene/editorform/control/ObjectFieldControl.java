package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;

public class ObjectFieldControl extends FieldControl<ObjectFieldControl, ObjectProperty<JsonElement>, JsonElement> {


    private final EditorFormPane formPane;

    private ObjectProperty<JsonElement> valueProperty;

    public ObjectFieldControl(EditorFormPane editorFieldPane) {
        this.formPane = editorFieldPane;
        this.valueProperty.bind(editorFieldPane.persistedObjectProperty());
    }

    @Override
    protected void setupControl() {
        this.valueProperty = new SimpleObjectProperty<>();
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
    public ObjectProperty<JsonElement> valueProperty() {
        return this.valueProperty;
    }

    @Override
    public Node getNode() {
        return this.formPane;
    }

    @Override
    public boolean fulfillsRequired(JsonElement value) {
        if (value != null && value.isJsonObject()) {
            return !value.getAsJsonObject()
                    .isEmpty();
        }

        return false;
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
