package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MultiSelectionFieldContent<T> extends FieldControl<MultiSelectionFieldContent<T>, ListProperty<T>, ObservableList<T>> {
    private final Function<T, String> getId;

    private ListView<T> listView;
    private ListProperty<T> listProperty;

    public MultiSelectionFieldContent(List<T> items, Function<T, String> getId) {
        this.listView.setItems(FXCollections.observableArrayList(items));
        this.getId = getId;
    }

    @Override
    public Node getNode() {
        return this.listView;
    }

    @Override
    protected void setupControl() {
        this.listView = new ListView<>();
        this.listView.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        this.listProperty = new SimpleListProperty<>(this.listView.getSelectionModel()
                .getSelectedItems()
        );
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
        List<String> selectedNames = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                if (arrayElement.isJsonPrimitive()) {
                    selectedNames.add(arrayElement.getAsString());
                }
            }
        } else if (jsonElement.isJsonPrimitive()) {
            selectedNames.add(jsonElement.getAsString());
        }

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
        return this.listProperty;
    }

    @Override
    public boolean fulfillsRequired(ObservableList<T> value) {
        return !value.isEmpty();
    }

    public static MultiSelectionFieldContent<String> of(ListSelectionFileFieldDefinition definition) throws FormException {
        return new MultiSelectionFieldContent<>(
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
