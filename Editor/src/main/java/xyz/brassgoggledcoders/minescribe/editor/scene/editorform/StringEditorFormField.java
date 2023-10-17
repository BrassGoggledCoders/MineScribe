package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.StringField;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.StringFileField;

public class StringEditorFormField implements IEditorFormField<StringField> {
    private final StringFileField fileField;
    private final StringField field;

    public StringEditorFormField(StringFileField fileField) {
        this.fileField = fileField;
        this.field = Field.ofStringType(fileField.getDefaultValue())
                .label(fileField.getLabel());
    }

    @Override
    public IFileField getFileField() {
        return this.fileField;
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
