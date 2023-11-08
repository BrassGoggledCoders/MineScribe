package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.ILabeledContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.FieldControl;

public class EditorFileFieldPane<F extends FieldContent<F>> extends EditorFieldPane<F> {
    private final FileFieldInfo fieldInfo;
    private final F content;

    private final ListProperty<String> errorList;

    public EditorFileFieldPane(FileForm fileForm, FileFieldInfo fieldInfo, F content) {
        super(fileForm);
        this.fieldInfo = fieldInfo;
        this.content = content;
        this.errorList = new SimpleListProperty<>(FXCollections.observableArrayList());

        setup();
    }

    public ListProperty<String> errorListProperty() {
        return this.errorList;
    }

    private void setup() {
        this.content.withId(this.fieldInfo.field());
        if (this.content instanceof IValueContent<?> valueControl) {
            valueControl.withRequired(this.fieldInfo.required());

            this.changedProperty().bind(valueControl.changedProperty());
            this.validProperty().bind(Bindings.and(
                    valueControl.validProperty(),
                    Bindings.isEmpty(this.errorListProperty())
            ));
            if (this.content instanceof FieldControl<?, ?, ?> control && !this.fieldInfo.validations().isEmpty()) {
                control.valueProperty().addListener((observable, old, newValue) -> {
                    this.errorList.get().clear();
                    this.fieldInfo.validations()
                            .stream()
                            .map(validation -> validation.validate(newValue))
                            .filter(result -> !result.isValid())
                            .map(ValidationResult::getMessage)
                            .forEach(this.errorList.get()::add);
                });
            }
        }
        if (this.content instanceof ILabeledContent<?> labeledControl) {
            labeledControl.withLabel(this.fieldInfo.label());
            this.labelProperty().set(labeledControl.getLabel());
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
