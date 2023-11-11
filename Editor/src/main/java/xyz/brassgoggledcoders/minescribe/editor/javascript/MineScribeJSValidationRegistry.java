package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;

@SuppressWarnings("unused")
public class MineScribeJSValidationRegistry {
    public void register(String name, Codec<FieldValidation> fieldValidationCodec) {
        EditorRegistries.getValidationRegistry()
                .register(new ResourceId(name), fieldValidationCodec);
    }
}
