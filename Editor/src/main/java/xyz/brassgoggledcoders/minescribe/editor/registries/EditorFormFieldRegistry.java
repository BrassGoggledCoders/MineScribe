package xyz.brassgoggledcoders.minescribe.editor.registries;

import com.dlsc.formsfx.model.structure.Field;
import com.mojang.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;

public class EditorFormFieldRegistry extends Registry<String, EditorFormFieldTransform<?, ?, ?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorFormFieldRegistry.class);

    public EditorFormFieldRegistry() {
        super("editorFormField", Codec.STRING);
    }

    public <T extends IFileFieldDefinition, U extends IEditorFormField<F>, F extends Field<F>> void register(
            String name, EditorFormFieldTransform<T, U, F> transforms
    ) {
        this.getMap().put(name, transforms);
    }

    public IEditorFormField<?> createEditorFieldFor(IFileFieldDefinition fileFieldDefinition) {
        String key = Registries.getFileFieldCodecRegistry()
                .getKey(fileFieldDefinition.getCodec());
        if (key != null) {
            EditorFormFieldTransform<?, ?, ?> transform = this.getValue(key);
            if (transform != null) {
                return transform.transformField(fileFieldDefinition);
            } else {
                LOGGER.error("No Transform found for key {}", key);
            }
        } else {
            LOGGER.error("No key found for File Field Definition {}", fileFieldDefinition);
        }

        return null;
    }

    public void validate() {
        for (String key : Registries.getFileFieldCodecRegistry().getKeys()) {
            if (!this.hasKey(key)) {
                LOGGER.error("Failed to find value for key {}", key);
            }
        }
    }
}
