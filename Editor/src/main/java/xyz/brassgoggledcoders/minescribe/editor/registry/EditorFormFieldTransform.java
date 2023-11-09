package xyz.brassgoggledcoders.minescribe.editor.registry;

import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.functional.ThrowingFunction;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;

public record EditorFormFieldTransform<T extends IFileFieldDefinition, U extends FieldContent>(
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
