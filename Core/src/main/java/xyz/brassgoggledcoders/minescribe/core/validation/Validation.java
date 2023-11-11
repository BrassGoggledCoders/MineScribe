package xyz.brassgoggledcoders.minescribe.core.validation;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MappedKeyDispatchCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.function.Function;

public abstract class Validation<T> implements Function<T, ValidationResult> {
    @SuppressWarnings("RedundantTypeArguments")
    public static final Codec<Validation<?>> DIRECT_DISPATCH_CODEC = LazyCodec.of(() ->
            MappedKeyDispatchCodec.<Codec<? extends Validation<?>>, Validation<?>>dispatch(
                    Registries.getValidations()
                            .getCodec(),
                    Validation::getCodec,
                    Function.identity()
            )
    );

    public abstract ValidationResult validate(T value);

    @Override
    public ValidationResult apply(T t) {
        return validate(t);
    }

    public abstract Codec<? extends Validation<?>> getCodec();
}
