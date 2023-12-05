package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IHelpTextContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.ILabeledContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

public class EditorFileFieldPane<F extends FieldContent<F>> extends EditorFieldPane<F> {
    private final FileFieldInfo fieldInfo;
    private final F content;

    public EditorFileFieldPane(FileForm fileForm, FileFieldInfo fieldInfo, F content) {
        super(fileForm);
        this.fieldInfo = fieldInfo;
        this.content = content;

        setup();
    }

    private void setup() {
        this.content.withId(this.fieldInfo.field());
        if (this.content instanceof IValueContent<?, ?, ?> valueControl) {
            valueControl.withRequired(this.fieldInfo.required())
                    .withValidations(this.fieldInfo.validations());

            this.changedProperty().bind(valueControl.changedProperty());
            this.validProperty().bind(valueControl.validProperty());
            this.messagesProperty().bind(valueControl.messagesProperty());
        }
        if (this.content instanceof ILabeledContent<?> labeledControl) {
            labeledControl.withLabel(this.fieldInfo.label());
            this.labelProperty().set(labeledControl.getLabel());
        }
        if (this.content instanceof IHelpTextContent helpTextContent) {
            this.fieldInfo.helpText().ifPresent(helpTextContent::setHelpText);
        }

        SceneUtils.setAnchors(content.getNode());
        this.getChildren().add(content.getNode());
    }

    @Override
    public F getContent() {
        return content;
    }

    @Override
    public String getFieldName() {
        return this.fieldInfo.field();
    }

    @Override
    public int getSortOrder() {
        return this.fieldInfo.sortOrder();
    }

    @Override
    public String toString() {
        return "EditorFileFieldPane{" +
                "fieldInfo=" + fieldInfo +
                '}';
    }

    public static <C extends FieldContent<C>, U extends IFileFieldDefinition> EditorFileFieldPane<C> of(
            FileForm form, FileField<U> field
    ) throws FormException {
        C fieldContent = EditorRegistries.getEditorFormFieldRegistry()
                .createEditorFieldFor(field.definition());

        return new EditorFileFieldPane<>(form, field.info(), fieldContent);
    }
}
