package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;

import java.util.List;

public interface IValueContent<C extends IValueContent<C, P, V>, P extends ReadOnlyProperty<V>, V> {
    BooleanProperty changedProperty();

    BooleanProperty validProperty();

    P valueProperty();

    void load(JsonElement jsonElement);

    JsonElement save();

    void persist();

    void reset();

    C withRequired(boolean required);

    ListProperty<String> errorMessages();

    @SuppressWarnings("UnusedReturnValue")
    C withValidations(List<FieldValidation> validations);
}
