package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

public class CheckBoxFieldControl extends FieldControl<CheckBoxFieldControl, BooleanProperty, Boolean> {
    private final boolean defaultValue;

    private final CheckBox checkBox;

    public CheckBoxFieldControl(boolean defaultValue) {
        super();
        this.checkBox = new CheckBox();
        this.valueProperty()
                .set(defaultValue);
        this.defaultValue = defaultValue;
    }

    @Override
    protected Either<Boolean, ValidationResult> castObject(Object value) {
        return castObjectWithClass(value, Boolean.class);
    }

    @Override
    protected JsonElement saveControl() {
        return new JsonPrimitive(this.checkBox.isSelected());
    }

    @Override
    protected void loadControl(@Nullable JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            this.valueProperty()
                    .set(jsonElement.getAsBoolean());
        } else {
            this.valueProperty()
                    .set(this.defaultValue);
        }
    }

    @Override
    public BooleanProperty valueProperty() {
        return this.checkBox.selectedProperty();
    }

    @Override
    public Node getNode() {
        return this.checkBox;
    }

    @Override
    public boolean fulfillsRequired(Boolean value) {
        return value != null;
    }

    @Override
    public boolean containsUserData() {
        return this.defaultValue != this.checkBox.isSelected();
    }

    public static CheckBoxFieldControl of(CheckBoxFileFieldDefinition definition) {
        return new CheckBoxFieldControl(definition.defaultValue());
    }
}
