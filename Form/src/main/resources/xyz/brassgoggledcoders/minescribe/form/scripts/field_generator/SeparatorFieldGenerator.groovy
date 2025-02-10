package xyz.brassgoggledcoders.minescribe.form.scripts.field_generator

import groovy.transform.BaseScript
import javafx.beans.property.StringProperty
import javafx.scene.control.Separator
import xyz.brassgoggledcoders.minescribe.form.fieldgenerator.FieldContainer
import xyz.brassgoggledcoders.minescribe.form.script.FieldGeneratorScript

//noinspection GroovyUnusedAssignment
@BaseScript
FieldGeneratorScript script

id("minescribe:separator")
description("A separator")
hideLabel(true)
fieldCreator {
    return new FieldContainer(new Separator())
}


