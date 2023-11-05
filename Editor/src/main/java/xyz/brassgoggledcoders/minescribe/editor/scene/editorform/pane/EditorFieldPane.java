package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.dlsc.formsfx.model.structure.Field;
import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

public abstract class EditorFieldPane<F extends Field<F>> extends Pane {
    private final FileForm fileForm;
    private final BooleanProperty changed;
    private final BooleanProperty valid;

    private EditorFormPane formPane;

    public EditorFieldPane(FileForm fileForm) {
        this.fileForm = fileForm;
        this.changed = new SimpleBooleanProperty(false);
        this.valid = new SimpleBooleanProperty(true);
    }

    public FileForm getFileForm() {
        return fileForm;
    }

    public abstract F getField();

    public abstract void setValue(JsonElement jsonElement);

    public abstract JsonElement getValue();

    public void setFormPane(EditorFormPane formPane) {
        this.formPane = formPane;
    }

    public EditorFormPane getFormPane() {
        return this.formPane;
    }

    public void reset() {
        this.getField()
                .reset();
    }

    public void persist() {
        if (this.valid.get()) {
            this.getField()
                    .persist();
        }
    }

    public BooleanProperty validProperty() {
        return this.valid;
    }

    public boolean isValid() {
        return this.validProperty()
                .get();
    }

    public BooleanProperty changedProperty() {
        return this.changed;
    }

    public boolean isChanged() {
        return this.changedProperty()
                .get();
    }

    public abstract String getFieldName();
}
