package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.NumberFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

import java.util.Objects;

public class NumberFieldControl<N extends Number> extends FieldControl<NumberFieldControl<N>, ReadOnlyObjectProperty<N>, N> {
    private final Spinner<N> spinner = new Spinner<>();
    private final NumberFileFieldDefinition<N> definition;
    private final Class<N> numberClass;

    public NumberFieldControl(NumberFileFieldDefinition<N> definition, Class<N> numberClass, SpinnerValueFactory<N> valueFactory) {
        super();
        this.definition = definition;
        this.numberClass = numberClass;
        this.spinner.setValueFactory(valueFactory);
    }

    @Override
    protected Either<N, ValidationResult> castObject(Object value) {
        return castObjectWithClass(value, numberClass);
    }

    @Override
    protected JsonElement saveControl() {
        return new JsonPrimitive(this.spinner.getValue());
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        if (jsonElement != null) {
            this.spinner.getValueFactory()
                    .setValue(this.definition.convertNumber(jsonElement.getAsNumber()));
        }
    }

    @Override
    public ReadOnlyObjectProperty<N> valueProperty() {
        return this.spinner.valueProperty();
    }

    @Override
    public boolean containsUserData() {
        return !Objects.equals(
                this.definition.getRange()
                        .start(),
                this.definition.convertNumber(this.valueProperty()
                        .get()
                )
        );
    }

    @Override
    public Node getNode() {
        return this.spinner;
    }

    @Override
    public boolean fulfillsRequired(N value) {
        return value != null;
    }
}
