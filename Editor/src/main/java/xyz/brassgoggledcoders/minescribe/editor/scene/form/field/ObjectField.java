package xyz.brassgoggledcoders.minescribe.editor.scene.form.field;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.dlsc.formsfx.model.util.BindingMode;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.ObjectFieldControl;
import xyz.brassgoggledcoders.minescribe.editor.util.FormUtils;
import xyz.brassgoggledcoders.minescribe.editor.util.FormUtils.FormSetup;

public class ObjectField extends Field<ObjectField> {
    private final FileForm fileForm;

    private FormSetup formSetup;
    private FormSetup serializerSetup;

    public ObjectField(@NotNull FileForm fileForm) {
        this.fileForm = fileForm;
        this.rendererSupplier = ObjectFieldControl::new;
    }

    public FormSetup getFormSetup() {
        if (this.formSetup == null) {
            this.formSetup = this.createForm();
        }
        return this.formSetup;
    }

    public Form getForm() {
        return this.getFormSetup().form();
    }

    public Form getSerializerFormSetup() {
        return null;
    }

    public void reloadForm(@Nullable SerializerType newSerializerType) {
        this.getFormSetup()
                .serializerFieldOpt()
                .ifPresent(serializerField -> serializerField.selectionProperty()
                        .setValue(newSerializerType)
                );
    }

    private FormSetup createForm() {
        FormSetup newFormSetup = FormUtils.setupForm(this.fileForm, false);
        newFormSetup.serializerFieldOpt()
                .ifPresent(serializerField -> serializerField.selectionProperty()
                        .addListener(this::onSerializerChange)
                );
        return newFormSetup;
    }

    public SerializerInfo getSerializerInfo() {
        return this.fileForm.getSerializer()
                .orElse(null);
    }

    @Override
    public void setBindingMode(BindingMode newValue) {
        this.getForm()
                .getFields()
                .forEach(field -> field.setBindingMode(newValue));
    }

    @Override
    protected boolean validate() {
        return this.getForm().isValid();
    }

    @Override
    public void persist() {
        this.formSetup.serializerFieldOpt().ifPresent(SingleSelectionField::persist);
        this.getForm().persist();
    }

    @Override
    public void reset() {
        this.formSetup.serializerFieldOpt().ifPresent(SingleSelectionField::reset);
        this.getForm().reset();
    }

    public void onSerializerChange(ObservableValue<? extends SerializerType> observable, SerializerType oldValue, SerializerType newValue) {
        this.reloadForm(newValue);
    }
}
