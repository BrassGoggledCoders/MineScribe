package xyz.brassgoggledcoders.minescribe.editor.scene.form.field;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.util.BindingMode;
import com.google.gson.JsonObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.ObjectFieldControl;
import xyz.brassgoggledcoders.minescribe.editor.util.FormUtils;
import xyz.brassgoggledcoders.minescribe.editor.util.FormUtils.FormSetup;

public class ObjectField extends Field<ObjectField> {
    private final ObjectType objectType;
    private final ObjectProperty<JsonObject> persistedObject;

    private FormSetup formSetup;
    private final ObjectProperty<FormSetup> serializerSetup;

    public ObjectField(@NotNull ObjectType objectType) {
        this.objectType = objectType;
        this.rendererSupplier = ObjectFieldControl::new;
        this.persistedObject = new SimpleObjectProperty<>();
        this.serializerSetup = new SimpleObjectProperty<>();
    }

    public FormSetup getFormSetup() {
        if (this.formSetup == null) {
            this.formSetup = this.createForm();
            this.formSetup.serializerFieldOpt()
                    .ifPresent(serializerField -> this.reloadForm(serializerField.getSelection()));
        }
        return this.formSetup;
    }

    public Form getForm() {
        return this.getFormSetup()
                .form();
    }

    public Form getSerializerForm() {
        if (this.serializerSetup.get() != null) {
            return this.serializerSetup.get()
                    .form();
        } else {
            return null;
        }
    }

    public ObjectProperty<FormSetup> serializerSetupProperty() {
        return this.serializerSetup;
    }

    public void reloadForm(@Nullable SerializerType newSerializerType) {
        if (newSerializerType != null) {
            this.serializerSetup.set(FormUtils.setupForm(
                    newSerializerType.fileForm(),
                    () -> Registries.getSerializerTypes()
                            .getFor(newSerializerType)
            ));
        } else {
            this.serializerSetup.set(null);
        }
    }

    private FormSetup createForm() {
        FormSetup newFormSetup = FormUtils.setupForm(
                this.objectType.fileForm(),
                () -> Registries.getSerializerTypes()
                        .getFor(this.objectType)
        );
        newFormSetup.serializerFieldOpt()
                .ifPresent(serializerField -> serializerField.selectionProperty()
                        .addListener(this::onSerializerChange)
                );
        return newFormSetup;
    }

    @Override
    public void setBindingMode(BindingMode newValue) {

    }

    @Override
    protected boolean validate() {
        return this.getForm().isValid() && (this.getSerializerForm() == null || this.getSerializerForm().isValid());
    }

    @Override
    public void persist() {
        this.getForm()
                .persist();
        if (this.getSerializerForm() != null) {
            this.getSerializerForm().persist();
        }
    }

    @Override
    public void reset() {
        this.getForm()
                .reset();
        if (this.getSerializerForm() != null) {
            this.getSerializerForm().reset();
        }
    }

    public void onSerializerChange(ObservableValue<? extends SerializerType> observable, SerializerType oldValue, SerializerType newValue) {
        this.reloadForm(newValue);
    }

    public void setValue(JsonObject jsonObject) {
        this.persistedObject.setValue(jsonObject);
        FormUtils.tryLoadForm(this.getFormSetup(), jsonObject);
    }

    public JsonObject getPersistedValue() {
        return this.persistedObject.get();
    }
}
