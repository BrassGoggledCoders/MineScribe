package xyz.brassgoggledcoders.minescribe.form.script

import javafx.scene.layout.Region
import xyz.brassgoggledcoders.minescribe.form.Id
import xyz.brassgoggledcoders.minescribe.form.fieldgenerator.FieldContainer

import java.util.function.Function

abstract class FieldGeneratorScript extends Script {
    void id(String id) {
        FieldGeneratorMetadata generatorMetadata = this.getProperty("metadata") as FieldGeneratorMetadata
        generatorMetadata.setId(new Id(id))
    }

    void id(String domain, String path) {
        FieldGeneratorMetadata generatorMetadata = this.getProperty("metadata") as FieldGeneratorMetadata
        generatorMetadata.setId(new Id(domain, path))
    }

    void hideLabel(boolean hideLabel) {
        FieldGeneratorMetadata generatorMetadata = this.getProperty("metadata") as FieldGeneratorMetadata
        generatorMetadata.setHideLabel(hideLabel)
    }

    void description(String description) {
        FieldGeneratorMetadata generatorMetadata = this.getProperty("metadata") as FieldGeneratorMetadata
        generatorMetadata.setDescription(description)
    }

    void fieldCreator(Function<FieldInfo, FieldContainer> fieldCreator) {
        FieldGeneratorMetadata generatorMetadata = this.getProperty("metadata") as FieldGeneratorMetadata
        generatorMetadata.fieldCreator = fieldCreator
    }
}

