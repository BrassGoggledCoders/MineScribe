package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.StringField;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.StringFileFieldDefinition;

public class StringEditorFormField implements IEditorFormField<StringField> {
    private final StringField field;

    public StringEditorFormField(StringFileFieldDefinition fileField) {
        this.field = Field.ofStringType(fileField.defaultValue());
    }

    @Override
    public StringField asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        this.field.valueProperty()
                .setValue(jsonElement.getAsString());
    }

    @Override
    public JsonElement saveAsJson() {
        return new JsonPrimitive(this.field.getValue());
    }
}
