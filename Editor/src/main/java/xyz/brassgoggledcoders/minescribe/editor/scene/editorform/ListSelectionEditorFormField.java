package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.MultiSelectionField;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileField;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.ListSelectionControl;

import java.util.List;

public class ListSelectionEditorFormField implements IEditorFormField<MultiSelectionField<String>> {
    private final ListSelectionFileField fileField;
    private final MultiSelectionField<String> field;

    public ListSelectionEditorFormField(ListSelectionFileField fileField) {
        this.fileField = fileField;
        this.field = Field.ofMultiSelectionType(fileField.getListNames()
                        .stream()
                        .map(Registries.getFormLists()::getValue)
                        .map(FormList::values)
                        .flatMap(List::stream)
                        .toList()
                )
                .render(ListSelectionControl::new)
                .label(fileField.getLabel());
    }

    @Override
    public IFileField getFileField() {
        return this.fileField;
    }

    @Override
    public MultiSelectionField<String> asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                this.field.getSelection().add(arrayElement.getAsString());
            }
        }
    }

    @Override
    public JsonElement saveAsJson() {
        String[] values = this.field.getSelection().toArray(String[]::new);
        JsonArray jsonArray = new JsonArray(values.length);
        for (String value : values) {
            jsonArray.add(value);
        }
        return jsonArray;
    }
}
