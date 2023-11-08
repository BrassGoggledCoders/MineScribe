package xyz.brassgoggledcoders.minescribe.core.validation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Function;

public abstract class FieldValidation extends Validation<Object> {
    @SuppressWarnings("RedundantTypeArguments")
    public static final Codec<FieldValidation> CODEC = LazyCodec.of(() -> Registries.getValidations()
            .getCodec()
            .<Validation<?>>dispatch(
                    Validation::getCodec,
                    Function.identity()
            )
            .flatXmap(
                    validation -> {
                        if (validation instanceof FieldValidation fieldValidation) {
                            return DataResult.success(fieldValidation);
                        } else {
                            ResourceId key = Registries.getValidations()
                                    .getKey(validation.getCodec());
                            return DataResult.error("%s is not a Field Validation".formatted(key));
                        }
                    },
                    DataResult::success
            )
    );

    public static final Codec<List<FieldValidation>> LIST_CODEC = CODEC.listOf();
}
