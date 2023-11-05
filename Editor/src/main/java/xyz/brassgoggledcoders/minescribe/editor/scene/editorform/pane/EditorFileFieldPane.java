package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.view.controls.SimpleControl;
import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.Pane;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;

public class EditorFileFieldPane<F extends Field<F>> extends EditorFieldPane<F> {
    private final FileFieldInfo fieldInfo;
    private final IEditorFormField<F> editorField;
    private final F field;

    public EditorFileFieldPane(FileForm fileForm, FileFieldInfo fieldInfo, IEditorFormField<F> editorField) {
        super(fileForm);
        this.fieldInfo = fieldInfo;
        this.editorField = editorField;
        this.field = editorField.asField();

        setup();
    }

    private void setup() {
        this.field.id(this.fieldInfo.field())
                .required(this.fieldInfo.required())
                .label(this.fieldInfo.label());

        this.changedProperty().bind(this.field.changedProperty());
        this.validProperty().bind(this.field.validProperty());

        SimpleControl<F> control = this.field.getRenderer();
        control.setField(field);
        this.getChildren().add(control);
    }

    @Override
    public F getField() {
        return field;
    }

    public void setValue(JsonElement jsonElement) {
        this.editorField.loadFromJson(jsonElement);
    }

    public JsonElement getValue() {
        return this.editorField.saveAsJson();
    }

    @Override
    public String getFieldName() {
        return this.fieldInfo.field();
    }

    public static EditorFileFieldPane<?> of(FileForm form, FileField<?> field) throws FormException {
        IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                .createEditorFieldFor(field.definition());

        return new EditorFileFieldPane<>(form, field.info(), editorFormField);
    }
}
