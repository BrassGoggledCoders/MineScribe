package xyz.brassgoggledcoders.minescribe.script

import groovy.transform.BaseScript
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane

//noinspection GroovyUnusedAssignment
@BaseScript BaseFieldGeneratorScript baseScript

name("minescribe:text")
description("A Simple Text Box")
controlSupplier {
    new TextField("Hi")
}