package xyz.brassgoggledcoders.minescribe.script;

import groovy.lang.Script;
import javafx.scene.control.Control;

import java.util.function.Supplier;

public abstract class BaseFieldGeneratorScript extends Script {
    void name(String name){
        if (this.getProperty("metadata") instanceof FieldGeneratorMetaData fieldGeneratorMetaData) {
            fieldGeneratorMetaData.setName(name);
        }
    }

    void description(String description){
        if (this.getProperty("metadata") instanceof FieldGeneratorMetaData fieldGeneratorMetaData) {
            fieldGeneratorMetaData.setDescription(description);
        }
    }

    void controlSupplier(Supplier<Control> controlSupplier){
        if (this.getProperty("metadata") instanceof FieldGeneratorMetaData fieldGeneratorMetaData) {
            fieldGeneratorMetaData.setControlSupplier(controlSupplier);
        }
    }
}
