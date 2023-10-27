package xyz.brassgoggledcoders.minescribe.editor.scene.editorform;

import com.dlsc.formsfx.model.structure.DataField;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.Property;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.NumberFileField;

import java.util.function.Function;

public class NumberEditorFormField<N extends NumberFileField<T>, T extends Number, P extends Property<Number>, F extends DataField<P, T, F>>
        implements IEditorFormField<F> {
    private final N fileField;
    private final F field;

    public NumberEditorFormField(N fileField, Function<N, F> fSupplier) {
        this.fileField = fileField;
        this.field = fSupplier.apply(this.fileField);
    }

    @Override
    public IFileField getFileField() {
        return this.fileField;
    }

    @Override
    public F asField() {
        return this.field;
    }

    @Override
    public void loadFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            this.asField().valueProperty()
                    .setValue(this.fileField.convertNumber(jsonElement.getAsNumber()));
        }
    }

    @Override
    public JsonElement saveAsJson() {
        return new JsonPrimitive(this.asField().getValue());
    }
}
