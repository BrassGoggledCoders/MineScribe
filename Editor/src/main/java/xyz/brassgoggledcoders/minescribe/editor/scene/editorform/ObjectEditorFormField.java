package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.google.gson.JsonElement;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ObjectField;

public class ObjectEditorFormField implements IEditorFormField<ObjectField> {
    private final ObjectField field;

    private ObjectEditorFormField(ObjectField objectField) {
        this.field = objectField;
    }

    @Override
    public ObjectField asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            this.field.setValue(jsonElement.getAsJsonObject());
        }
    }

    @Override
    public JsonElement saveAsJson() {
        return this.field.getPersistedValue();
    }

    public static ObjectEditorFormField of(ReferencedObjectFileFieldDefinition fieldDefinition) throws FormException {
        ObjectType objectType = Registries.getObjectTypes()
                .getValue(fieldDefinition.objectId());

        if (objectType != null) {
            return new ObjectEditorFormField(ObjectField.of(objectType));
        } else {
            throw new FormException("No Object with Id: %s".formatted(fieldDefinition.objectId()));
        }
    }
}
