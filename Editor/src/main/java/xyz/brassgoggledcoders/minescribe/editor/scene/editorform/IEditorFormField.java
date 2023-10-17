package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonElement;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;

public interface IEditorFormField<F extends Field<F>> {

    IFileField getFileField();

    F asField();

    void loadFromJson(JsonElement jsonElement);

    JsonElement saveAsJson();
}
