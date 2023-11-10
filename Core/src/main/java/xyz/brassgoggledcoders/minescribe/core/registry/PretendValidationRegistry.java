package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.validation.PretendValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.util.function.BiFunction;
import java.util.function.Function;

public class PretendValidationRegistry extends Registry<ResourceId, Codec<? extends Validation<?>>> {
    private final JsonCodec<? extends Validation<Object>> codec;

    public PretendValidationRegistry() {
        super("pretendValidations", ResourceId.CODEC);
        this.codec = new JsonCodec<>(
                PretendValidation::new,
                PretendValidation::getInternals
        );
    }

    @Override
    public Codec<? extends Validation<?>> getValue(ResourceId key) {
        return this.codec;
    }
}
