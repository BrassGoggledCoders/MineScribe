package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.StringFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

public class StringFieldControl extends FieldControl<StringFieldControl, StringProperty, String> {
    private final TextField textField;
    private final InputGroup inputGroup;

    public StringFieldControl(String text) {
        super();
        this.textField = new TextField();
        HBox.setHgrow(this.textField, Priority.ALWAYS);
        FontIcon resetIcon = new FontIcon(Feather.REPEAT);
        Button resetButton = new Button("", resetIcon);
        resetButton.getStyleClass().add(Styles.BUTTON_ICON);
        this.inputGroup = new InputGroup(textField, resetButton);
        this.valueProperty()
                .set(text);
    }

    @Override
    protected Either<String, ValidationResult> castObject(Object value) {
        return castObjectWithClass(value, String.class);
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

    public static StringFieldControl of(StringFileFieldDefinition definition) {
        return new StringFieldControl(definition.defaultValue());
    }
}
