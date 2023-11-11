package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
import javafx.css.PseudoClass;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.util.List;

public interface IValueContent<C extends IValueContent<C, P, V>, P extends ReadOnlyProperty<V>, V> {
    PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

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
    C withValidations(List<Validation<?>> validations);

    boolean hasValidations();

    void validate();
}
