package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonElement;
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
    private final Map<UUID, IEditorFormField<?>> childFields;

    public ListOfEditorFormField(ListOfFileField fileField) {
        this.fileField = fileField;
        this.field = new ListOfFields()
                .label(this.fileField.getLabel())
                .minimumFields(fileField.getMinimum())
                .maximumFields(fileField.getMaximum())
                .fieldSupplier(this::createField);
        this.childFields = new HashMap<>();
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

    }

    @Override
    public JsonElement saveAsJson() {
        return null;
    }

    private Field<?> createField() {
        IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                .createEditorFieldFor(this.fileField.getChildField());

        if (editorFormField != null) {
            Field<?> childField = editorFormField.asField();
            UUID id = UUID.randomUUID();
            childField.id(id.toString());
            this.childFields.put(id, editorFormField);
            return childField;
        } else {
            return null;
        }
    }
}
