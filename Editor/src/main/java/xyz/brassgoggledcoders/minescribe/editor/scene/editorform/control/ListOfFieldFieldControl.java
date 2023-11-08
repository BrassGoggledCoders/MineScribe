package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListOfFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.FieldListControl;

import java.util.ArrayList;
import java.util.List;

public class ListOfFieldFieldControl extends FieldControl<ListOfFieldFieldControl, ListProperty<FieldContent<?>>, ObservableList<FieldContent<?>>> {

    private final FieldListControl fieldListControl;
    private ListProperty<FieldContent<?>> fieldContents;

    public ListOfFieldFieldControl(IFileFieldDefinition definition) {
        this.fieldListControl = new FieldListControl(definition);
        this.fieldContents.bind(this.fieldListControl.contentsProperty());
    }

    @Override
    public Node getNode() {
        return fieldListControl;
    }

    @Override
    protected void setupControl() {
        this.fieldContents = new SimpleListProperty<>();
    }

    @Override
    protected JsonElement saveControl() {
        JsonArray jsonArray = new JsonArray();
        for (FieldContent<?> fieldContent : this.fieldContents) {
            if (fieldContent instanceof IValueContent<?> valueContent) {
                jsonArray.add(valueContent.save());
            }
        }
        return jsonArray;
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        List<JsonElement> jsonElementList = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                jsonElementList.add(arrayElement);
            }
        } else {
            jsonElementList.add(jsonElement);
        }

        for (int x = 0; x < jsonElementList.size(); x++) {
            FieldContent<?> fieldContent;
            if (x < this.fieldContents.size()) {
                fieldContent = this.fieldContents.get(x);
            } else {
                fieldContent = this.fieldListControl.addNewContent();
            }

            if (fieldContent instanceof IValueContent<?> valueContent) {
                valueContent.load(jsonElementList.get(x));
            }
        }
    }

    @Override
    public ListProperty<FieldContent<?>> valueProperty() {
        return this.fieldContents;
    }

    @Override
    protected boolean fulfillsRequired(ObservableList<FieldContent<?>> value) {
        return !value.isEmpty();
    }

    public static ListOfFieldFieldControl of(ListOfFileFieldDefinition definition) {
        return new ListOfFieldFieldControl(definition.getChildField());
    }
}
