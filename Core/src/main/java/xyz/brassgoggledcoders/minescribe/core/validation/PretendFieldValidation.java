package xyz.brassgoggledcoders.minescribe.core.validation;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

public class PretendFieldValidation extends FieldValidation {
    private final JsonElement internals;
    private final Codec<? extends FieldValidation> codec;

    public PretendFieldValidation(JsonElement internals, Codec<FieldValidation> codec) {
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
    public Codec<? extends FieldValidation> getCodec() {
        return this.codec;
    }
}
