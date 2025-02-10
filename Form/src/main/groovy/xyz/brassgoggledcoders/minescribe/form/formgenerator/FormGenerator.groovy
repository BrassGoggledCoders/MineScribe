package xyz.brassgoggledcoders.minescribe.form.formgenerator

import javafx.beans.property.MapProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableMap
import xyz.brassgoggledcoders.minescribe.form.Id
import xyz.brassgoggledcoders.minescribe.form.script.FieldGeneratorMetadata

class FormGenerator {
    private final FormGeneratorProvider formGeneratorProvider
    private MapProperty<String, Object> structureInput
    private ObjectProperty<FieldGenerator> rootFieldGenerator

    FormGenerator(FormGeneratorProvider formGeneratorProvider) {
        this.formGeneratorProvider = formGeneratorProvider
        this.structureInput = new SimpleMapProperty<>(this, "structureInput", null)
        this.rootFieldGenerator = new SimpleObjectProperty<>(this, "rootFieldGenerator", null)
        this.structureInput.addListener(new ChangeListener<ObservableMap<String, Object>>() {
            @Override
            void changed(
                    ObservableValue<? extends ObservableMap<String, Object>> observableValue,
                    ObservableMap<String, Object> oldValue,
                    ObservableMap<String, Object> newValue
            ) {
                if (newValue == null) {
                    rootFieldGeneratorProperty()
                            .set(null)
                } else {
                    if (!newValue.type) {
                        newValue.type = 'minescribe:object'
                    }

                    rootFieldGeneratorProperty()
                            .set(new FieldGenerator())
                }
            }
        })
    }

    MapProperty<String, Object> structureInputProperty() {
        return this.structureInput
    }

    ObjectProperty<FieldGenerator> rootFieldGeneratorProperty() {
        return this.rootFieldGenerator
    }
}
