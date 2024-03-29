package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListOfFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldMessagesEvent;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.FieldListControl;

import java.util.*;

public class ListOfFieldFieldControl extends FieldControl<ListOfFieldFieldControl, ListProperty<Property<?>>, ObservableList<Property<?>>> {

    private final FieldListControl fieldListControl;
    private final List<FieldValidation> fieldValidations;

    public ListOfFieldFieldControl(IFileFieldDefinition definition) {
        super();
        this.fieldListControl = new FieldListControl(definition, this::getFieldValidations);
        this.fieldValidations = new ArrayList<>();
        this.fieldListControl.addEventHandler(FieldMessagesEvent.EVENT_TYPE, this::handleFieldMessagesEvent);
    }

    public void handleFieldMessagesEvent(FieldMessagesEvent event) {
        this.validate();
    }

    @Override
    public Node getNode() {
        return fieldListControl;
    }

    @Override
    protected JsonElement saveControl() {
        JsonArray jsonArray = new JsonArray();
        for (FieldContent<?> fieldContent : this.fieldListControl.contentsProperty()) {
            if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                JsonElement jsonElement = valueContent.save();
                if (jsonElement != null && !jsonElement.isJsonNull()) {
                    jsonArray.add(jsonElement);
                }
            }
        }
        return jsonArray;
    }

    @Override
    protected void loadControl(JsonElement jsonElement) {
        List<JsonElement> jsonElementList = new ArrayList<>();
        if (jsonElement != null) {
            if (jsonElement.isJsonArray()) {
                for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                    jsonElementList.add(arrayElement);
                }
            } else {
                jsonElementList.add(jsonElement);
            }
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
    protected Set<FieldMessage> additionalChecks(ObservableList<Property<?>> newValue) {
        Set<FieldMessage> fieldMessages = new HashSet<>();
        if (this.requiredProperty().get() || !this.fieldListControl.contentsProperty().isEmpty()) {
            if (newValue.size() < this.fieldListControl.minimumFieldsProperty().get()) {
                fieldMessages.add(new FieldMessage(
                        this.getFieldInfo(),
                        MessageType.ERROR,
                        "Not enough values"
                ));
            }
        }


        if (newValue.size() > this.fieldListControl.maxFieldsProperty().get()) {
            fieldMessages.add(new FieldMessage(
                    this.getFieldInfo(),
                    MessageType.ERROR,
                    "Too many values"
            ));
        }
        long numberInvalid = this.fieldListControl.invalidChildren().get();
        if (numberInvalid > 0) {
            fieldMessages.add(new FieldMessage(
                    this.getFieldInfo(),
                    MessageType.ERROR,
                    "Field contains %s invalid fields".formatted(numberInvalid)
            ));
        }

        fieldMessages.addAll(super.additionalChecks(newValue));

        return fieldMessages;
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
    public void persist() {
        this.fieldListControl.contentsProperty()
                .forEach(fieldContent -> {
                    if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                        if (valueContent.containsUserData()) {
                            valueContent.persist();
                        }
                    }
                });
        super.persist();
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
        return value != null;
    }

    @Override
    public ListOfFieldFieldControl withLabel(Label label) {
        this.fieldListControl.textProperty()
                .setValue(Optional.ofNullable(label)
                        .map(Label::getText)
                        .orElse("")
                );
        return super.withLabel(label);
    }

    @Override
    public void validateAll() {
        super.validateAll();
        if (this.requiredProperty().get() || this.containsUserData()) {
            for (FieldContent<?> fieldContent : this.fieldListControl.contentsProperty()) {
                if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                    valueContent.validateAll();
                }
            }
        }

    }

    @Override
    public boolean containsUserData() {
        return this.fieldListControl.contentsProperty()
                .stream()
                .anyMatch(fieldContent -> {
                    if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                        return valueContent.containsUserData();
                    }

                    return false;
                });
    }

    public static ListOfFieldFieldControl of(ListOfFileFieldDefinition definition) {
        return new ListOfFieldFieldControl(definition.getChildField());
    }
}
