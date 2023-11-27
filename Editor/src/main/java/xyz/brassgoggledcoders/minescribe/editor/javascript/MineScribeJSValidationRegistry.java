package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;

@SuppressWarnings("unused")
public class MineScribeJSValidationRegistry {
    public void register(String name, Codec<FieldValidation> fieldValidationCodec) {
        Registries.getValidationCodecRegistry()
                .register(new ResourceId(name), fieldValidationCodec);
    }
}
