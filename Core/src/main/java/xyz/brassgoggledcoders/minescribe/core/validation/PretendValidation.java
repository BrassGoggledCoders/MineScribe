package xyz.brassgoggledcoders.minescribe.core.validation;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

public class PretendValidation<T> extends Validation<T> {
    private final JsonElement internals;
    private final Codec<Validation<?>> codec;

    public PretendValidation(JsonElement internals, Codec<Validation<?>> codec) {
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
    public Codec<Validation<?>> getCodec() {
        return this.codec;
    }
}
