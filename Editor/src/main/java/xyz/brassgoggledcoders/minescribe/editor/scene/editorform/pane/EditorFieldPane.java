package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.gson.JsonElement;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IFieldContentNode;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

public abstract class EditorFieldPane<F extends FieldContent<F>> extends AnchorPane implements IFieldContentNode {
    private final FileForm fileForm;
    private final BooleanProperty changed;
    private final BooleanProperty valid;
    private final ObjectProperty<Label> label;
    private final SetProperty<FieldMessage> messages;

    private EditorFormPane formPane;

    public EditorFieldPane(FileForm fileForm) {
        this.fileForm = fileForm;
        this.changed = new SimpleBooleanProperty(false);
        this.valid = new SimpleBooleanProperty(true);
        this.label = new SimpleObjectProperty<>();
        this.messages = new SimpleSetProperty<>(FXCollections.observableSet());

        this.setPadding(new Insets(5));
    }

    public SetProperty<FieldMessage> messagesProperty() {
        return this.messages;
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

    @Override
    public FieldContent<?> getFieldContent() {
        return this.getContent();
    }

    public abstract String getFieldName();

    public abstract int getSortOrder();

    public void validate() {
        if (this.getFieldContent() instanceof IValueContent<?,?,?> valueContent) {
            valueContent.validate();
        }
    }
}
