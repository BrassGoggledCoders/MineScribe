package xyz.brassgoggledcoders.minescribe.form.scripts.field_generator

import groovy.transform.BaseScript
import javafx.scene.control.TextField
import xyz.brassgoggledcoders.minescribe.form.fieldgenerator.FieldContainer
import xyz.brassgoggledcoders.minescribe.form.script.FieldGeneratorScript

//noinspection GroovyUnusedAssignment
@BaseScript
FieldGeneratorScript script

id("minescribe:text")
description("A Basic Text Field")
hideLabel(true)
fieldCreator {
    TextField textField = new TextField()
    textField.textProperty()
            .set(it.input.defaultValue?.toString())
    textField.textProperty()
            .addListener {

            }
    return new FieldContainer(
            textField,
            (value) -> {
                switch (value) {
                    case String:
                        textField.textProperty()
                                .set(value)
                }
            },
            {
                return textField.textProperty()
                        .get()
            }
    )
}


