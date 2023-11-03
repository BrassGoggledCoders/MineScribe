package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.SingleSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.Collections;

public class SingleSelectionEditorFormField implements IEditorFormField<SingleSelectionField<String>> {
    private final SingleSelectionFileFieldDefinition fileField;
    private final SingleSelectionField<String> field;

    public SingleSelectionEditorFormField(SingleSelectionFileFieldDefinition fileField) {
        this.fileField = fileField;
        this.field = Field.ofSingleSelectionType(Registries.getFormLists()
                .getOptionalValue(fileField.listId())
                .map(FormList::values)
                .orElse(Collections.emptyList())
        );
    }

    @Override
    public SingleSelectionFileFieldDefinition getFileFieldDefinition() {
        return this.fileField;
    }

    @Override
    public SingleSelectionField<String> asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            this.field.selectionProperty().set(jsonPrimitive.getAsString());
        }
    }

    @Override
    public JsonElement saveAsJson() {
        String selected = this.field.getSelection();
        if (selected != null) {
            return new JsonPrimitive(selected);
        } else {
            return JsonNull.INSTANCE;
        }
    }
}
