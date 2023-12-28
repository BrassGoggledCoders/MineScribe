package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.SearchableComboBox;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.SingleSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.FormListValue;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellConverter;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SingleSelectionFieldControl<T> extends FieldControl<SingleSelectionFieldControl<T>, ObjectProperty<T>, T> {
    private final ComboBox<T> comboBox = new SearchableComboBox<>();
    private final Function<T, String> getId;
    private final Class<T> tClass;

    public SingleSelectionFieldControl(List<T> items, Function<T, String> getId, Function<T, FancyText> getLabel, Class<T> tClass) {
        super();
        this.tClass = tClass;
        this.comboBox.setItems(FXCollections.observableArrayList(items));
        this.setLabelMaker(getLabel);
        this.getId = getId;
    }

    @Override
    protected JsonElement saveControl() {
        if (this.valueProperty().get() != null) {
            return new JsonPrimitive(getId.apply(this.valueProperty().get()));
        }
        return JsonNull.INSTANCE;
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            String id = jsonElement.getAsString();
            for (T value : this.comboBox.getItems()) {
                if (value != null) {
                    if (getId.apply(value).equals(id)) {
                        this.comboBox.selectionModelProperty()
                                .get()
                                .select(value);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public ObjectProperty<T> valueProperty() {
        return this.comboBox.valueProperty();
    }

    @Override
    public boolean containsUserData() {
        return this.valueProperty().get() != null;
    }

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return this.comboBox.itemsProperty();
    }

    @Override
    public Node getNode() {
        return this.comboBox;
    }

    @Override
    public boolean fulfillsRequired(T value) {
        return value != null;
    }

    public void setLabelMaker(Function<T, FancyText> labelMaker) {
        this.comboBox.setConverter(new LabeledCellConverter<>(labelMaker));
        this.comboBox.setCellFactory(new LabeledCellFactory<>(labelMaker));
    }

    public static SingleSelectionFieldControl<FormListValue> of(SingleSelectionFileFieldDefinition definition) throws FormException {
        try {
            List<FormListValue> values = new ArrayList<>();
            for (IFormList<?> formList : definition.formLists()) {
                values.addAll(formList.getFormListValues());
            }
            values.add(0, null);
            return new SingleSelectionFieldControl<>(
                    values,
                    FormListValue::id,
                    FormListValue::label,
                    FormListValue.class
            );
        } catch (Exception e) {
            throw new FormException("Found error while gathering list values", e);
        }
    }

    public static <T> SingleSelectionFieldControl<T> of(List<T> items, Function<T, String> getId, Function<T, FancyText> getLabel, Class<T> tClass) {
        return new SingleSelectionFieldControl<>(
                items,
                getId,
                getLabel,
                tClass
        );
    }
}
