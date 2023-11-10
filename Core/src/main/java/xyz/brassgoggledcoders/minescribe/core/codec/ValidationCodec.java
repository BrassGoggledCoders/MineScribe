package xyz.brassgoggledcoders.minescribe.core.codec;

import com.google.gson.JsonNull;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.PretendFieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

public class ValidationCodec<T extends Validation<U>, U> implements Codec<T> {
    public static ValidationCodec<FieldValidation, Object> FIELD_CODEC = new ValidationCodec<>(
            FieldValidation.DIRECT_DISPATCH_CODEC,
            new JsonCodec<>(
                    (PretendFieldValidation::new),
                    fieldValidation -> {
                        if (fieldValidation instanceof PretendFieldValidation pretendFieldValidation) {
                            return pretendFieldValidation.getInternals();
                        } else {
                            return JsonNull.INSTANCE;
                        }
                    }
            )
    );

    private final Codec<T> realCodec;
    private final JsonCodec<T> jsonCodec;

    public ValidationCodec(Codec<T> realCodec, JsonCodec<T> jsonCodec) {
        this.realCodec = realCodec;
        this.jsonCodec = jsonCodec;
    }


    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        if (Registries.getValidationsNullable() != null) {
            return this.realCodec.decode(ops, input);
        } else {
            return this.jsonCodec.decode(ops, input);
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        if (Registries.getValidationsNullable() != null) {
            return this.realCodec.encode(input, ops, prefix);
        } else {
            return this.jsonCodec.encode(input, ops, prefix);
        }
    }
}
