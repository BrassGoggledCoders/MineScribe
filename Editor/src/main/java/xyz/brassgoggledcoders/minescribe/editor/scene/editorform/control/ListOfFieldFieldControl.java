package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListOfFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.message.MineScribeMessage;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.FieldListControl;

import java.util.ArrayList;
import java.util.List;

public class ListOfFieldFieldControl extends FieldControl<ListOfFieldFieldControl, ListProperty<Property<?>>, ObservableList<Property<?>>> {

    private final FieldListControl fieldListControl;
    private final List<FieldValidation> fieldValidations;

    public ListOfFieldFieldControl(IFileFieldDefinition definition) {
        super();
        this.fieldListControl = new FieldListControl(definition, this::getFieldValidations);
        this.fieldValidations = new ArrayList<>();
        this.fieldListControl.invalidChildren()
                .addListener((observable, oldValue, newValue) -> updateInvalidChildren(newValue.longValue()));
    }

    @Override
    public Node getNode() {
        return fieldListControl;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Either<ObservableList<Property<?>>, ValidationResult> castObject(Object value) {
        if (value instanceof ObservableList<?> list) {
            return Either.left((ObservableList<Property<?>>) list);
        }
        return Either.right(ValidationResult.error("Value not a list"));
    }

    @Override
    protected JsonElement saveControl() {
        JsonArray jsonArray = new JsonArray();
        for (FieldContent<?> fieldContent : this.fieldListControl.contentsProperty()) {
            if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
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
            if (x < this.contentProperty().size()) {
                fieldContent = this.contentProperty().get(x);
            } else {
                fieldContent = this.fieldListControl.addNewContent();
            }

            if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                valueContent.load(jsonElementList.get(x));
            }
        }
    }

    @Override
    protected void checkValid(ObservableList<Property<?>> newValue) {
        super.checkValid(newValue);

        long numberInvalid = this.fieldListControl.invalidChildren().get();
        if (numberInvalid > 0) {
            this.messagesProperty().add(new MineScribeMessage(
                    MessageType.ERROR,
                    null,
                    this.getLabelString(),
                    "Field contains %s invalid fields".formatted(numberInvalid)
            ));
        }
    }

    private void updateInvalidChildren(long numberInvalid) {
        this.messagesProperty().clear();
        if (numberInvalid > 0) {
            this.messagesProperty().add(new MineScribeMessage(
                    MessageType.ERROR,
                    null,
                    this.getLabelString(),
                    "Field contains %s invalid fields".formatted(numberInvalid)
            ));
        }
    }

    @Override
    public ListOfFieldFieldControl withValidations(List<Validation<?>> validations) {
        for (Validation<?> validation : validations) {
            if (validation instanceof FieldValidation fieldValidation) {
                this.fieldValidations.add(fieldValidation);
            }
        }
        return this;
    }

    @Override
    public boolean hasValidations() {
        return true;
    }

    private List<FieldValidation> getFieldValidations() {
        return this.fieldValidations;
    }

    public ListProperty<FieldContent<?>> contentProperty() {
        return this.fieldListControl.contentsProperty();
    }

    @Override
    public ListProperty<Property<?>> valueProperty() {
        return this.fieldListControl.valueProperty();
    }

    @Override
    public boolean fulfillsRequired(ObservableList<Property<?>> value) {
        return !value.isEmpty();
    }

    public static ListOfFieldFieldControl of(ListOfFileFieldDefinition definition) {
        return new ListOfFieldFieldControl(definition.getChildField());
    }
}
