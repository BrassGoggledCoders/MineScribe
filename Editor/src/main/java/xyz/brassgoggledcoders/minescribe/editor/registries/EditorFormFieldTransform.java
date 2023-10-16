package xyz.brassgoggledcoders.minescribe.editor.registries;

import com.dlsc.formsfx.model.structure.Field;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;

import java.util.function.Function;

public record EditorFormFieldTransform<T extends IFileField, U extends IEditorFormField<F>, F extends Field<F>>(
        Class<T> fileFieldClass,
        Function<T, U> createEditorFormField
) {
    public U transformField(IFileField fileField) {
        if (this.fileFieldClass().isInstance(fileField)) {
            return this.createEditorFormField.apply(this.fileFieldClass().cast(fileField));
        }
        throw new IllegalStateException("Invalid File Field for this transformer");
    }
}
