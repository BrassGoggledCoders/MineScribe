package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.layout.InputGroup;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.StringFileFieldDefinition;

public class StringFieldControl extends FieldControl<StringFieldControl, StringProperty, String> {
    private final CustomTextField textField;
    private final InputGroup inputGroup;
    private final String defaultText;

    public StringFieldControl(String defaultText) {
        super();
        this.defaultText = defaultText;
        this.textField = new CustomTextField(defaultText);
        HBox.setHgrow(this.textField, Priority.ALWAYS);
        this.inputGroup = this.createInputGroup(this.textField);
    }

    @Override
    protected JsonElement saveControl() {
        String text = this.textField.getText();
        if (text != null && !text.isEmpty()) {
            return new JsonPrimitive(text);
        }
        return JsonNull.INSTANCE;
    }

    @Override
    protected void loadControl(@Nullable JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            this.valueProperty()
                    .set(jsonElement.getAsString());
        } else {
            this.valueProperty()
                    .set(null);
        }
    }

    @Override
    public StringProperty valueProperty() {
        return this.textField.textProperty();
    }

    @Override
    public Node getNode() {
        return this.inputGroup;
    }

    @Override
    public boolean fulfillsRequired(String value) {
        return value != null && !value.isEmpty();
    }

    @Override
    public boolean containsUserData() {
        return !this.defaultText.equals(this.valueProperty().get());
    }

    public static StringFieldControl of(StringFileFieldDefinition definition) {
        return new StringFieldControl(definition.defaultValue());
    }
}
