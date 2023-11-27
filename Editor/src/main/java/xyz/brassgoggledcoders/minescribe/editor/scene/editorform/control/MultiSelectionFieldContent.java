package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.FormListValue;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class MultiSelectionFieldContent<T> extends FieldControl<MultiSelectionFieldContent<T>, ListProperty<T>, ObservableList<T>> {
    private final ListView<T> listView;
    private final Function<T, String> getId;
    private final ListProperty<T> selectedValues;

    public MultiSelectionFieldContent(List<T> items, Function<T, String> getId, Function<T, String> getLabel) {
        super();
        this.listView = new ListView<>();
        this.listView.setItems(FXCollections.observableArrayList(items));
        this.listView.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        this.getId = getId;
        this.listView.setCellFactory(new LabeledCellFactory<>(getLabel));
        this.selectedValues = new SimpleListProperty<>(this.listView.getSelectionModel()
                .getSelectedItems()
        );
    }

    @Override
    public Node getNode() {
        return this.listView;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Either<ObservableList<T>, ValidationResult> castObject(Object value) {
        if (value instanceof ObservableList<?> list) {
            return Either.left((ObservableList<T>) list);
        }
        return Either.right(ValidationResult.error("Value not a list"));
    }

    @Override
    protected JsonElement saveControl() {
        JsonArray jsonArray = new JsonArray();
        for (T value : this.listView.selectionModelProperty().get().getSelectedItems()) {
            jsonArray.add(new JsonPrimitive(getId.apply(value)));
        }
        return jsonArray;
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        List<String> selectedNames = MineScribeJsonHelper.getAsStrings(jsonElement);

        this.listView.getSelectionModel()
                .clearSelection();
        if (!selectedNames.isEmpty()) {
            for (T object : this.listView.getItems()) {
                if (selectedNames.contains(getId.apply(object))) {
                    this.listView.getSelectionModel()
                            .select(object);
                }
            }
        }

    }

    @Override
    public ListProperty<T> valueProperty() {
        return this.selectedValues;
    }

    @Override
    public boolean fulfillsRequired(ObservableList<T> value) {
        return !value.isEmpty();
    }

    public static MultiSelectionFieldContent<FormListValue> of(ListSelectionFileFieldDefinition definition) throws FormException {
        try {
            List<FormListValue> values = new ArrayList<>();
            for (IFormList<?> formList : definition.listNames()) {
                values.addAll(formList.getFormListValues());
            }
            return new MultiSelectionFieldContent<>(
                    values,
                    FormListValue::id,
                    FormListValue::label
            );
        } catch (Exception e) {
            throw new FormException(e.getMessage(), e);
        }
    }
}
