package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import com.google.gson.JsonElement;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.ILabeledContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class FieldControl<C extends FieldControl<C, P, V>, P extends ReadOnlyProperty<V>, V>
        extends FieldContent<C> implements IValueContent<C, P, V>, ILabeledContent<C> {
    private final BooleanProperty valid;
    private final BooleanProperty changed;
    private final BooleanProperty required;

    private final ObjectProperty<Label> label;

    private final ObjectProperty<JsonElement> persistedValue;
    private final SetProperty<String> errorList;
    private final List<FieldValidation> validations;

    protected FieldControl() {
        this.valid = new SimpleBooleanProperty(true);
        this.changed = new SimpleBooleanProperty(false);
        this.required = new SimpleBooleanProperty(false);
        this.label = new SimpleObjectProperty<>();
        this.persistedValue = new SimpleObjectProperty<>();
        this.errorList = new SimpleSetProperty<>(FXCollections.observableSet());
        this.validations = new ArrayList<>();

        this.setupControl();
        this.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.checkValid(newValue);
                    changedProperty()
                            .set(true);
                });
        this.valid.bind(Bindings.isEmpty(this.errorList));
    }

    protected abstract void setupControl();

    private void checkValid(V newValue) {
        List<String> newErrors = new ArrayList<>();
        for (FieldValidation fieldValidation : this.validations) {
            ValidationResult result = fieldValidation.validate(newValue);
            if (!result.isValid()) {
                newErrors.add(result.getMessage());
            }
        }
        if (this.required.get() && !this.fulfillsRequired(newValue)) {
            String label = this.label.get() != null ? this.label.get().getText() : "Field";
            newErrors.add(label + " is Required");
        }
        this.errorList.get().removeIf(Predicate.not(newErrors::contains));
        this.errorList.get().addAll(newErrors);
    }

    @SuppressWarnings("unchecked")
    public C withLabel(Label label) {
        this.label.set(label);
        return (C) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C withRequired(boolean required) {
        this.required.set(required);
        return (C) this;
    }

    @Override
    @Nullable
    public Label getLabel() {
        return label.get();
    }

    @Override
    public BooleanProperty changedProperty() {
        return changed;
    }

    @Override
    public BooleanProperty validProperty() {
        return valid;
    }

    @Override
    public void load(JsonElement jsonElement) {
        this.persistedValue.set(jsonElement);
        this.loadControl(jsonElement);
    }

    @Override
    public JsonElement save() {
        return this.persistedValue.get();
    }

    @Override
    public void persist() {
        if (this.validProperty().get()) {
            JsonElement saved = this.saveControl();
            this.persistedValue.set(saved);
        }
    }

    @Override
    public void reset() {
        this.loadControl(this.persistedValue.get());
    }

    @Override
    public SetProperty<String> errorListProperty() {
        return this.errorList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C withValidations(List<FieldValidation> validations) {
        this.validations.clear();
        this.validations.addAll(validations);
        return (C) this;
    }

    protected abstract JsonElement saveControl();

    protected abstract void loadControl(JsonElement jsonElement);

    public abstract boolean fulfillsRequired(V value);
}
