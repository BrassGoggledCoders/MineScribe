package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.controlsfx.control.ListSelectionView;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.FormListValue;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.LabeledCellFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListSelectionFieldContent<T extends Comparable<T>> extends FieldControl<ListSelectionFieldContent<T>, ObjectProperty<ObservableList<T>>, ObservableList<T>> {
    private final ListSelectionView<T> listSelection = new ListSelectionView<>();
    private final Function<T, String> getId;
    private final List<T> items;

    public ListSelectionFieldContent(List<T> items, Function<T, String> getId, Function<T, FancyText> getLabel) {
        super();
        this.items = items;
        this.listSelection.setSourceItems(FXCollections.observableArrayList(items));
        this.getId = getId;
        this.listSelection.setCellFactory(new LabeledCellFactory<>(getLabel));
    }

    @Override
    public void finishSetup() {
        super.finishSetup();
        this.valueProperty()
                .get()
                .addListener((ListChangeListener<T>) c -> ListSelectionFieldContent.super.onChanged());
        this.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.addListener((ListChangeListener<T>) c -> ListSelectionFieldContent.super.onChanged());
                    }
                }));
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
        this.items.sort(Comparable::compareTo);
        this.listSelection.getSourceItems().setAll(this.items);
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
    public boolean containsUserData() {
        return !this.valueProperty()
                .get()
                .isEmpty();
    }

    @Override
    public boolean fulfillsRequired(ObservableList<T> value) {
        return !value.isEmpty();
    }

    public static ListSelectionFieldContent<FormListValue> of(ListSelectionFileFieldDefinition definition) throws FormException {
        try {
            List<FormListValue> values = new ArrayList<>();
            for (IFormList<?> formList : definition.listNames()) {
                values.addAll(formList.getFormListValues());
            }
            return new ListSelectionFieldContent<>(
                    values,
                    FormListValue::id,
                    FormListValue::label
            );
        } catch (Exception e) {
            throw new FormException(e.getMessage(), e);
        }
    }
}
