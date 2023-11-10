package xyz.brassgoggledcoders.minescribe.core.validation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

public class PretendValidation extends Validation<Object> {
    private final JsonElement internals;
    private final Codec<? extends Validation<?>> codec;

    public PretendValidation(JsonElement internals, Codec<PretendValidation> codec) {
        this.internals = internals;
        this.codec = codec;
    }

    public JsonElement getInternals() {
        return internals;
    }

    @Override
    public ValidationResult validate(Object value) {
        throw new UnsupportedOperationException("This is only for pretend");
    }

    @Override
    public Codec<? extends Validation<?>> getCodec() {
        return this.codec;
    }
}
