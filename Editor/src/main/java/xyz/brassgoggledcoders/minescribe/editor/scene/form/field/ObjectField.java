package xyz.brassgoggledcoders.minescribe.editor.scene.form.field;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.util.BindingMode;
import com.google.gson.JsonObject;
import javafx.scene.Node;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.ObjectFieldControl;

public class ObjectField extends Field<ObjectField> {
    private final EditorFormPane editorFormPane;

    private ObjectField(EditorFormPane editorFormPane) {
        this.editorFormPane = editorFormPane;
        this.rendererSupplier = ObjectFieldControl::new;

        this.changedProperty().bind(this.editorFormPane.changedProperty());
        this.validProperty().bind(this.editorFormPane.validProperty());
    }

    @Override
    public void setBindingMode(BindingMode newValue) {

    }

    @Override
    protected boolean validate() {
        return this.valid.get();
    }

    @Override
    public void persist() {
        if (this.validate()) {
            this.editorFormPane.persist();
        }
    }

    @Override
    public void reset() {
        this.editorFormPane.reset();
    }

    public void setValue(JsonObject jsonObject) {
        this.editorFormPane.setPersistedObject(jsonObject);
    }

    public JsonObject getPersistedValue() {
        return this.editorFormPane.persistedObjectProperty()
                .get();
    }

    public EditorFormPane getEditorForm() {
        return this.editorFormPane;
    }

    public static ObjectField of(ObjectType objectType) throws FormException {
        return new ObjectField(EditorFormPane.of(
                objectType.fileForm(),
                () -> Registries.getSerializerTypes().getFor(objectType),
                null
        ));
    }
}
