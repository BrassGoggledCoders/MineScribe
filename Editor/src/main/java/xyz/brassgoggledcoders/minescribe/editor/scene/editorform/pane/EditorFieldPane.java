package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

public abstract class EditorFieldPane<F extends FieldContent<F>> extends AnchorPane {
    private final FileForm fileForm;
    private final BooleanProperty changed;
    private final BooleanProperty valid;
    private final ObjectProperty<Label> label;

    private EditorFormPane formPane;

    public EditorFieldPane(FileForm fileForm) {
        this.fileForm = fileForm;
        this.changed = new SimpleBooleanProperty(false);
        this.valid = new SimpleBooleanProperty(true);
        this.label = new SimpleObjectProperty<>();

        this.setPadding(new Insets(5));
    }

    public FileForm getFileForm() {
        return fileForm;
    }

    public abstract F getContent();

    public void setValue(JsonElement jsonElement) {
        if (this.getContent() instanceof IValueContent<?, ?, ?> valueControl) {
            valueControl.load(jsonElement);
        }
    }

    public JsonElement getValue() {
        if (this.getContent() instanceof IValueContent<?, ?, ?> valueControl) {
            return valueControl.save();
        }
        return null;
    }

    public void setFormPane(EditorFormPane formPane) {
        this.formPane = formPane;
    }

    public EditorFormPane getFormPane() {
        return this.formPane;
    }

    public void reset() {
        if (this.getContent() instanceof IValueContent<?, ?, ?> valueControl) {
            valueControl.reset();
        }
    }

    public void persist() {
        if (this.getContent() instanceof IValueContent<?, ?, ?> valueControl) {
            valueControl.persist();
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

    public ObjectProperty<Label> labelProperty() {
        return this.label;
    }

    public abstract String getFieldName();

    public abstract int getSortOrder();
}
