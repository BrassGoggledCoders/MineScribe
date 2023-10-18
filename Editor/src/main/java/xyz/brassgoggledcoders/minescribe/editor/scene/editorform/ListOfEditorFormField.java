package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.collections.ListChangeListener;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListOfFileField;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ListOfFields;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListOfEditorFormField implements IEditorFormField<ListOfFields> {
    private final ListOfFileField fileField;
    private final ListOfFields field;
    private final Map<String, IEditorFormField<?>> childFields;

    public ListOfEditorFormField(ListOfFileField fileField) {
        this.fileField = fileField;
        this.childFields = new HashMap<>();
        this.field = new ListOfFields()
                .label(this.fileField.getLabel())
                .minimumFields(fileField.getMinimum())
                .maximumFields(fileField.getMaximum())
                .fieldSupplier(this::createField);

        this.field.valueProperty().addListener((ListChangeListener<Field<?>>) c -> {
            if (c.next() && c.wasRemoved()) {
                for (Field<?> removedField : c.getRemoved()) {
                    this.childFields.remove(removedField.getID());
                }
            }
        });
    }

    @Override
    public IFileField getFileField() {
        return this.fileField;
    }

    @Override
    public ListOfFields asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                if (i >= this.field.valueProperty().size()) {
                    this.field.requestNewField();
                }
                Field<?> childField = this.field.getValue().get(i);
                IEditorFormField<?> editorFormField = this.childFields.get(childField.getID());
                editorFormField.loadFromJson(array.get(i));
            }
        }
    }

    @Override
    public JsonElement saveAsJson() {
        JsonArray jsonArray = new JsonArray();
        for (Field<?> childField: this.field.valueProperty()) {
            IEditorFormField<?> editorFormField = this.childFields.get(childField.getID());
            if (editorFormField != null) {
                jsonArray.add(editorFormField.saveAsJson());
            }
        }
        return jsonArray;
    }

    private Field<?> createField() {
        IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                .createEditorFieldFor(this.fileField.getChildField());

        if (editorFormField != null) {
            Field<?> childField = editorFormField.asField();
            String id = UUID.randomUUID().toString();
            childField.id(id);
            this.childFields.put(id, editorFormField);
            return childField;
        } else {
            return null;
        }
    }
}
