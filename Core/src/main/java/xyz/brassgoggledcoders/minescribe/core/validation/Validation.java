package xyz.brassgoggledcoders.minescribe.core.validation;

import com.mojang.serialization.Codec;

import java.util.function.Function;

public abstract class Validation<T> implements Function<T, ValidationResult> {
    public abstract ValidationResult validate(T value);

    @Override
    public ValidationResult apply(T t) {
        return validate(t);
    }

    public abstract Codec<? extends Validation<?>> getCodec();
}
