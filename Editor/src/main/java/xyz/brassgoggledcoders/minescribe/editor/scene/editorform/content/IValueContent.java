package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
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

    SetProperty<String> errorListProperty();

    @SuppressWarnings("UnusedReturnValue")
    C withValidations(List<FieldValidation> validations);
}
