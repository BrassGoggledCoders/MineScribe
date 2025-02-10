package xyz.brassgoggledcoders.minescribe.form.formgenerator

import xyz.brassgoggledcoders.minescribe.form.Id

import xyz.brassgoggledcoders.minescribe.form.script.FieldGeneratorMetadata
import xyz.brassgoggledcoders.minescribe.form.script.FieldGeneratorScript

import java.util.function.Function
import java.util.function.Supplier

class FormGeneratorProvider implements Supplier<FormGenerator> {
    private final Function<InputStreamReader, Script> scriptParser

    private final Map<Id, FieldGeneratorMetadata> fieldGeneratorMetadataRegistry

    FormGeneratorProvider(Function<InputStreamReader, Script> scriptParser) {
        this.scriptParser = scriptParser
        this.fieldGeneratorMetadataRegistry = new HashMap<>()
    }

    void provideScripts(InputStream scriptStream) {
        try (InputStreamReader reader = new InputStreamReader(scriptStream)) {
            Script script = scriptParser.apply(reader)
            if (script instanceof FieldGeneratorScript) {
                FieldGeneratorMetadata fieldGeneratorMetadata = new FieldGeneratorMetadata()
                script.setProperty("metadata", fieldGeneratorMetadata)
                script.run()
                if (fieldGeneratorMetadata.isValid()) {
                    this.fieldGeneratorMetadataRegistry.put(fieldGeneratorMetadata.id, fieldGeneratorMetadata)
                }
            }
        }
    }

    FieldGeneratorMetadata getFieldGeneratorMetadata(Id id) {
        return fieldGeneratorMetadataRegistry.get(id)
    }

    @Override
    FormGenerator get() {
        return new FormGenerator(this)
    }
}
