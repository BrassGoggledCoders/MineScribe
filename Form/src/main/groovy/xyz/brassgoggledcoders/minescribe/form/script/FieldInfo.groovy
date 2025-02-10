package xyz.brassgoggledcoders.minescribe.form.script


import javafx.scene.layout.Pane
import xyz.brassgoggledcoders.minescribe.form.formgenerator.FormGenerator

class FieldInfo {
    final Pane parent
    final FormGenerator formGenerator
    final Map<String, Object> input

    FieldInfo(Pane parent, FormGenerator formGenerator, Map<String, Object> input) {
        this.parent = parent
        this.formGenerator = formGenerator
        this.input = input
    }
}