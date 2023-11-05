package xyz.brassgoggledcoders.minescribe.editor.registries;

import com.dlsc.formsfx.model.structure.Field;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.functional.ThrowingFunction;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;

import java.util.function.Function;

public record EditorFormFieldTransform<T extends IFileFieldDefinition, U extends IEditorFormField<F>, F extends Field<F>>(
        Class<T> fileFieldDefinition,
        ThrowingFunction<T, U, FormException> createEditorFormField
) {
    public U transformField(IFileFieldDefinition fileFieldDefinition) throws FormException {
        if (this.fileFieldDefinition().isInstance(fileFieldDefinition)) {
            return this.createEditorFormField.apply(this.fileFieldDefinition().cast(fileFieldDefinition));
        }
        throw new FormException("Invalid File Field for this transformer: %s".formatted(fileFieldDefinition));
    }
}
