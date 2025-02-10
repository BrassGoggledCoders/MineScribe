package xyz.brassgoggledcoders.minescribe.form.formgenerator


import javafx.beans.property.MapProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import org.jetbrains.annotations.Nullable

import java.util.function.Function

class FieldGenerator implements Function<String, FieldGenerator> {
    private final FormGenerator formGenerator
    @Nullable
    private FieldGenerator parent
    @Nullable
    private StringProperty name
    private MapProperty<String, Object> structureInput
    private ObservableMap<StringProperty, FieldGenerator> children

    FieldGenerator(FormGenerator formGenerator) {
        this(formGenerator, null, null)
    }

    FieldGenerator(FormGenerator formGenerator, @Nullable FieldGenerator parent, @Nullable String name) {
        this.formGenerator = formGenerator
        this.parent = parent
        this.name = new SimpleStringProperty(name)
        this.structureInput = new SimpleMapProperty<>(this, "structureInput", null)
        this.children = new SimpleMapProperty<>(FXCollections.observableHashMap())
    }

    MapProperty<String, Object> structureInputProperty() {
        return this.structureInput
    }

    @Override
    FieldGenerator apply(String name) {
        FieldGenerator formGenerator = new FieldGenerator(this.formGenerator, this, name)
        this.children.put(formGenerator.name, formGenerator)
        return formGenerator
    }
}
