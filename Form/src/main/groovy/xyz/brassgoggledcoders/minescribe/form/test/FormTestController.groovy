package xyz.brassgoggledcoders.minescribe.form.test

import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import javafx.fxml.FXML
import xyz.brassgoggledcoders.minescribe.form.ModData
import xyz.brassgoggledcoders.minescribe.form.formgenerator.FormGeneratorProvider
import xyz.brassgoggledcoders.minescribe.form.formgenerator.ScriptCollector
import xyz.brassgoggledcoders.minescribe.form.layout.FormPane

class FormTestController {
    @FXML
    private FormPane formString

    private final GroovyShell groovyShell
    private final FormGeneratorProvider formGeneratorProvider

    FormTestController() {
        this.groovyShell = new GroovyShell()
        this.formGeneratorProvider = new FormGeneratorProvider(this.groovyShell::parse)
        ScriptCollector.collectFromModData(this.formGeneratorProvider::provideScripts, List.of(ModData.minescribeData()))
    }

    @FXML
    private void initialize() {
        this.formString.formGeneratorProperty()
                .set(this.formGeneratorProvider.get())
        Map<String, Object> json = [
                label : "Hello",
                fields: [
                        [
                                label: "Text",
                                type : "minescribe:text"
                        ]
                ]
        ]
        ObservableMap<String, Object> hashMap = FXCollections.observableHashMap()
        hashMap.putAll(json)
        this.formString.structureInputProperty()
                .set(hashMap)
    }
}
