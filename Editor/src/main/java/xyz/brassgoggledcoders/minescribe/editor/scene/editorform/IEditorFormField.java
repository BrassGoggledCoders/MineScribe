package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonObject;

public interface IEditorFormField<F extends Field<F>> {

    F asField();

    void loadFromJson(JsonObject jsonObject);

    void saveToJson(JsonObject jsonObject);
}
