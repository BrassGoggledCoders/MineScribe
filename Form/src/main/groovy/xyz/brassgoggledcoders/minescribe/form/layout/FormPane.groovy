package xyz.brassgoggledcoders.minescribe.form.layout

import com.fasterxml.jackson.databind.JsonNode
import javafx.beans.property.MapProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import xyz.brassgoggledcoders.minescribe.form.formgenerator.FormGenerator

class FormPane extends BorderPane {
    private ScrollPane scrollPane
    private HBox buttonPane
    private Button saveButton
    private Button resetButton
    private Button clearButton

    private ObjectProperty<FormGenerator> formGenerator
    private MapProperty<String, Object> structureInput

    FormPane() {
        this.scrollPane = new ScrollPane()
        this.buttonPane = new HBox()

        this.setCenter(this.scrollPane)
        this.setBottom(this.buttonPane)

        this.saveButton = new Button("Save")
        this.resetButton = new Button("Reset")
        this.clearButton = new Button("Clear")

        this.buttonPane.getChildren().addAll(this.saveButton, this.resetButton, this.clearButton)

        this.structureInput = new SimpleMapProperty<>(this, "input", null)
        this.formGenerator = new SimpleObjectProperty<>(this, "formGenerator", null)
        this.formGenerator.addListener(new ChangeListener<FormGenerator>() {
            @Override
            void changed(ObservableValue<? extends FormGenerator> observable, FormGenerator oldValue, FormGenerator newValue) {
                if (oldValue != null) {
                    oldValue.structureInputProperty()
                            .unbind()
                }
                if (newValue != null) {
                    newValue.structureInputProperty()
                            .bind(FormPane.this.structureInputProperty())
                }
            }
        })
    }

    ObjectProperty<FormGenerator> formGeneratorProperty() {
        return this.formGenerator
    }

    MapProperty<String, Object> structureInputProperty() {
        return this.structureInput
    }
}
