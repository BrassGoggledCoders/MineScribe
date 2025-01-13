package xyz.brassgoggledcoders.minescribe.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.script.BaseFieldGeneratorScript;
import xyz.brassgoggledcoders.minescribe.script.FieldGeneratorMetaData;
import xyz.brassgoggledcoders.minescribe.service.scripting.GroovyService;

@Component
public class EditorController {
    private final GroovyService groovyService;

    @FXML
    private HBox tab1Content;

    public EditorController(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    @FXML
    public void clicked() {
        if (this.groovyService.parse(this.getClass().getResource()) instanceof BaseFieldGeneratorScript baseFieldGeneratorScript) {
            baseFieldGeneratorScript.setProperty("metadata", new FieldGeneratorMetaData());
            baseFieldGeneratorScript.run();
            if (baseFieldGeneratorScript.getProperty("metadata") instanceof FieldGeneratorMetaData fieldGeneratorMetaData) {
                System.out.println(fieldGeneratorMetaData.getName());
                System.out.println(fieldGeneratorMetaData.getDescription());
                this.tab1Content.getChildren().addLast(fieldGeneratorMetaData.getControlSupplier().get());
            }
        }
    }
}
