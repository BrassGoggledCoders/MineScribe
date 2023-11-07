package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.SingleSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellConverter;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellFactory;

import java.util.List;
import java.util.function.Function;

public class SingleSelectionFieldControl<T> extends FieldControl<SingleSelectionFieldControl<T>, ObjectProperty<T>, T> {
    private final Function<T, String> getId;
    private ComboBox<T> comboBox;

    public SingleSelectionFieldControl(List<T> items, Function<T, String> getId) {
        this.comboBox.setItems(FXCollections.observableArrayList(items));
        this.getId = getId;
    }

    @Override
    protected void setupControl() {
        this.comboBox = new ComboBox<>();
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
                if (getId.apply(value).equals(id)) {
                    this.comboBox.selectionModelProperty()
                            .get()
                            .select(value);
                    break;
                }
            }
        }
    }

    @Override
    public ObjectProperty<T> valueProperty() {
        return this.comboBox.valueProperty();
    }

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return this.comboBox.itemsProperty();
    }

    @Override
    public Node getNode() {
        return this.comboBox;
    }

    @Override
    protected boolean fulfillsRequired(T value) {
        return value != null;
    }

    public void setLabelMaker(Function<T, String> labelMaker) {
        this.comboBox.setConverter(new LabeledCellConverter<>(labelMaker));
        this.comboBox.setCellFactory(new LabeledCellFactory<>(labelMaker));
    }

    public static SingleSelectionFieldControl<String> of(SingleSelectionFileFieldDefinition definition) throws FormException {
        return new SingleSelectionFieldControl<>(
                Registries.getFormLists()
                        .getOptionalValue(definition.listId())
                        .map(FormList::values)
                        .orElseThrow(() -> new FormException("Failed to find List for Id: " + definition.listId())),
                Function.identity()
        );
    }

    public static <T> SingleSelectionFieldControl<T> of(List<T> items, Function<T, String> getId) {
        return new SingleSelectionFieldControl<>(
                items,
                getId
        );
    }
}
