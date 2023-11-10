package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.ListSelectionView.MoveToTarget;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;

import java.util.List;
import java.util.function.Function;

public class ListSelectionFieldContent<T> extends FieldControl<ListSelectionFieldContent<T>, ListProperty<T>, ObservableList<T>> {
    private final Function<T, String> getId;

    private final List<T> items;
    private ListSelectionView<T> listSelection;
    private ListProperty<T> listProperty;


    public ListSelectionFieldContent(List<T> items, Function<T, String> getId) {
        this.items = items;
        this.listSelection.setSourceItems(FXCollections.observableArrayList(items));
        this.getId = getId;
        this.listSelection.getActions()
                .stream()
                .filter(MoveToTarget.class::isInstance)
                .map(MoveToTarget.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Move To Target Action Found"));
    }

    @Override
    public Node getNode() {
        return this.listSelection;
    }

    @Override
    protected void setupControl() {
        this.listSelection = new ListSelectionView<>();
        this.listProperty = new SimpleListProperty<>(this.listSelection.getTargetItems());
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
    public ListProperty<T> valueProperty() {
        return this.listProperty;
    }

    @Override
    public boolean fulfillsRequired(ObservableList<T> value) {
        return !value.isEmpty();
    }

    public static ListSelectionFieldContent<String> of(ListSelectionFileFieldDefinition definition) throws FormException {
        return new ListSelectionFieldContent<>(
                definition.listNames()
                        .stream()
                        .map(Registries.getFormLists()::getValue)
                        .map(FormList::values)
                        .flatMap(List::stream)
                        .toList(),
                Function.identity()
        );
    }
}
