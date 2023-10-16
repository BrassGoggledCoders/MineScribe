package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.BooleanField;
import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonObject;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileField;

public class CheckBoxEditorFormField implements IEditorFormField<BooleanField> {
    private final CheckBoxFileField fileField;
    private final BooleanField field;

    public CheckBoxEditorFormField(CheckBoxFileField fileField) {
        this.fileField = fileField;
        this.field = Field.ofBooleanType(fileField.getDefaultValue())
                .label(fileField.getLabel());
    }

    @Override
    public BooleanField asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonObject jsonObject) {
        if (jsonObject.has(this.fileField.getField())) {
            field.valueProperty().set(jsonObject.get(this.fileField.getField()).getAsBoolean());
        }
    }

    @Override
    public void saveToJson(JsonObject jsonObject) {
        jsonObject.addProperty(this.fileField.getField(), this.field.getValue());
    }
}
