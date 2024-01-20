package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;


public class MineScribeJSValidationRegistry {
    private final Provider<Registry<ResourceId, Codec<? extends Validation<?>>>> validationRegistry;

    @Inject
    public MineScribeJSValidationRegistry(Provider<Registry<ResourceId, Codec<? extends Validation<?>>>> validationRegistry) {
        this.validationRegistry = validationRegistry;
    }

    @SuppressWarnings("unused")
    public void register(String name, Codec<FieldValidation> fieldValidationCodec) {
        validationRegistry.get()
                .register(new ResourceId(name), fieldValidationCodec);
    }
}
