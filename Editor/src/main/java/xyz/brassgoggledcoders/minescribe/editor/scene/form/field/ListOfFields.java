package xyz.brassgoggledcoders.minescribe.editor.scene.form.field;

import com.dlsc.formsfx.model.structure.DataField;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.MultiSelectionField;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.SimpleFieldListControl;

import java.util.function.Supplier;

public class ListOfFields extends DataField<ListProperty<Field<?>>, ObservableList<Field<?>>, ListOfFields> {
    private final SimpleIntegerProperty minimumFields = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty maximumFields = new SimpleIntegerProperty(Integer.MAX_VALUE);

    private final ObjectProperty<Supplier<Field<?>>> fieldSupplier = new SimpleObjectProperty<>();

    public ListOfFields() {
        super(new SimpleListProperty<>(), new SimpleListProperty<>());
        this.value.setValue(FXCollections.observableArrayList());
        this.persistentValue.setValue(FXCollections.observableArrayList());
        this.render(SimpleFieldListControl::new);
    }

    public ListOfFields minimumFields(int minimumFields) {
        if (minimumFields < 0 || minimumFields > this.getMaximumFields()) {
            throw new IllegalStateException("Minimum Fields must be between 0 and %s (maximum value)".formatted(minimumFields));
        }
        this.minimumFields.set(minimumFields);
        return this;
    }

    public ListOfFields maximumFields(int maximumFields) {
        if (maximumFields < this.getMinimumFields()) {
            throw new IllegalStateException("Maximum Fields must be great than %s (minimum value)".formatted(this.getMinimumFields()));
        }
        this.minimumFields.set(maximumFields);
        return this;
    }

    public ListOfFields fieldSupplier(Supplier<Field<?>> fieldSupplier) {
        this.fieldSupplier.set(fieldSupplier);
        return this;
    }

    public int getMinimumFields() {
        return this.minimumFields.get();
    }

    public int getMaximumFields() {
        return this.maximumFields.get();
    }

    public void requestNewField() {
        Field<?> newField = this.fieldSupplier.get().get();
        if (newField != null) {
            this.getValue().add(newField);
        }
    }

    @Override
    public boolean validate() {
        boolean validate = super.validate();
        for (Field<?> field : this.getValue()) {
            if (field instanceof DataField<?, ?, ?> dataField) {
                validate &= dataField.validate();
            } else if (field instanceof SingleSelectionField<?> selectionField) {
                validate &= selectionField.validate();
            } else if (field instanceof MultiSelectionField<?> multiSelectionField) {
                validate &= multiSelectionField.validate();
            }
        }
        return validate;
    }

    @Override
    public void reset() {
        super.reset();
        for (Field<?> field : this.getValue()) {
            field.reset();
        }
    }

    @Override
    public void persist() {
        super.persist();
        for (Field<?> field : this.getValue()) {
            field.persist();
        }
    }
}
