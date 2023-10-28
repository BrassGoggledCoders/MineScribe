package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.google.gson.JsonElement;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileField;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ObjectField;

public class ObjectEditorFormField implements IEditorFormField<ObjectField> {
    private final ReferencedObjectFileField fileField;
    private final ObjectField field;

    public ObjectEditorFormField(ReferencedObjectFileField fileField) {
        this.fileField = fileField;
        this.field = new ObjectField(Registries.getObjectTypes()
                .getValue(fileField.getObjectId())
        );
    }

    @Override
    public IFileField getFileField() {
        return this.fileField;
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
}
