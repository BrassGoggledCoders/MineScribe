package xyz.brassgoggledcoders.minescribe.core.validation;

import com.mojang.serialization.Codec;

public abstract class Validation<T> {
    public abstract ValidationResult validate(T value);

    public abstract Codec<? extends Validation<T>> getCodec();
}
