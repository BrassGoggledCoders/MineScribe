package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.NumberFileFieldDefinition;

public class NumberFieldControl<N extends Number> extends FieldControl<NumberFieldControl<N>, ReadOnlyObjectProperty<N>, N> {
    private final NumberFileFieldDefinition<N> definition;

    private Spinner<N> spinner;

    public NumberFieldControl(NumberFileFieldDefinition<N> definition, SpinnerValueFactory<N> valueFactory) {
        this.definition = definition;
        this.spinner.setValueFactory(valueFactory);
    }

    @Override
    protected void setupControl() {
        this.spinner = new Spinner<>();
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
    public Node getNode() {
        return this.spinner;
    }

    @Override
    protected boolean fulfillsRequired(N value) {
        return false;
    }
}
