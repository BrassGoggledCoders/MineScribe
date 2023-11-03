package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.BooleanField;
import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;

public class CheckBoxEditorFormField implements IEditorFormField<BooleanField> {
    private final CheckBoxFileFieldDefinition fileField;
    private final BooleanField field;

    public CheckBoxEditorFormField(CheckBoxFileFieldDefinition fileField) {
        this.fileField = fileField;
        this.field = Field.ofBooleanType(fileField.defaultValue());
    }

    @Override
    public CheckBoxFileFieldDefinition getFileFieldDefinition() {
        return this.fileField;
    }

    @Override
    public BooleanField asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        field.valueProperty().set(jsonElement.getAsBoolean());
    }

    @Override
    public JsonElement saveAsJson() {
        return new JsonPrimitive(this.field.getValue());
    }
}
