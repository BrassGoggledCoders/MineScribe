package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.controlsfx.control.ListSelectionView;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.FormListValue;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellFactory;

import java.util.List;
import java.util.function.Function;

public class ListSelectionFieldContent<T> extends FieldControl<ListSelectionFieldContent<T>, ObjectProperty<ObservableList<T>>, ObservableList<T>> {
    private final ListSelectionView<T> listSelection = new ListSelectionView<>();
    private final Function<T, String> getId;
    private final List<T> items;


    public ListSelectionFieldContent(List<T> items, Function<T, String> getId, Function<T, String> getLabel) {
        super();
        this.items = items;
        this.listSelection.setSourceItems(FXCollections.observableArrayList(items));
        this.getId = getId;
        this.listSelection.setCellFactory(new LabeledCellFactory<>(getLabel));
    }

    @Override
    public Node getNode() {
        return this.listSelection;
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
        for (T value : this.listSelection.getTargetItems()) {
            jsonArray.add(new JsonPrimitive(getId.apply(value)));
        }
        return jsonArray;
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        List<String> selectedNames = MineScribeJsonHelper.getAsStrings(jsonElement);
        selectedNames.sort(String::compareTo);
        this.listSelection.setSourceItems(FXCollections.observableArrayList(this.items));
        this.listSelection.setTargetItems(FXCollections.observableArrayList());
        if (!selectedNames.isEmpty()) {
            for (T object : this.items) {
                if (selectedNames.contains(getId.apply(object))) {
                    this.listSelection.getSourceItems().remove(object);
                    this.listSelection.getTargetItems().add(object);
                }
            }
        }

    }

    @Override
    public ObjectProperty<ObservableList<T>> valueProperty() {
        return this.listSelection.targetItemsProperty();
    }

    @Override
    public boolean fulfillsRequired(ObservableList<T> value) {
        return !value.isEmpty();
    }

    public static ListSelectionFieldContent<FormListValue> of(ListSelectionFileFieldDefinition definition) throws FormException {
        return new ListSelectionFieldContent<>(
                definition.listNames()
                        .stream()
                        .map(IFormList::getFormListValues)
                        .flatMap(List::stream)
                        .toList(),
                FormListValue::id,
                FormListValue::label
        );
    }
}
