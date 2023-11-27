package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicStaticRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EditorFormFieldRegistry extends BasicStaticRegistry<String, EditorFormFieldTransform<?, ?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorFormFieldRegistry.class);

    public EditorFormFieldRegistry(Consumer<BiConsumer<String, EditorFormFieldTransform<?, ?>>> register) {
        super(RegistryNames.EDITOR_FIELD_TRANSFORMS, Codec.STRING, register);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <U extends FieldContent<U>> U createEditorFieldFor(IFileFieldDefinition fileFieldDefinition) throws FormException {
        String key = Registries.getFileFieldDefinitionCodecRegistry()
                .getKey(fileFieldDefinition.getCodec());
        if (key != null) {
            EditorFormFieldTransform<?, ?> transform = this.getValue(key);
            if (transform != null) {
                return (U) transform.transformField(fileFieldDefinition);
            } else {
                throw new FormException("No Transform found for key: %s".formatted(key));
            }
        } else {
            throw new FormException("No key found for File Field Definition: %s".formatted(fileFieldDefinition));
        }
    }

    @Override
    public void validate() {
        for (String key : Registries.getFileFieldDefinitionCodecRegistry().getKeys()) {
            if (!this.hasKey(key)) {
                LOGGER.error("Failed to find value for key {}", key);
            }
        }
    }
}
