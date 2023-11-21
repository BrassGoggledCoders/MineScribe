package xyz.brassgoggledcoders.minescribe.core.codec;

import com.google.gson.JsonNull;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import xyz.brassgoggledcoders.minescribe.core.MineScribeRuntime;
import xyz.brassgoggledcoders.minescribe.core.util.IDataObject;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.PretendValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.util.List;

public class ValidationCodec implements Codec<Validation<?>> {
    public static ValidationCodec CODEC = new ValidationCodec(
            FieldValidation.DIRECT_DISPATCH_CODEC,
            createJsonCodec()
    );

    public static Codec<List<Validation<?>>> LIST_CODEC = CODEC.listOf();


    private final Codec<Validation<?>> realCodec;
    private final JsonCodec<Validation<?>> jsonCodec;

    public ValidationCodec(Codec<Validation<?>> realCodec, JsonCodec<Validation<?>> jsonCodec) {
        this.realCodec = realCodec;
        this.jsonCodec = jsonCodec;
    }


    @Override
    public <T1> DataResult<Pair<Validation<?>, T1>> decode(DynamicOps<T1> ops, T1 input) {
        if (MineScribeRuntime.getRuntime() == MineScribeRuntime.APPLICATION) {
            return this.realCodec.decode(ops, input);
        } else {
            return this.jsonCodec.decode(ops, input);
        }
    }

    @Override
    public <T1> DataResult<T1> encode(Validation<?> input, DynamicOps<T1> ops, T1 prefix) {
        if (MineScribeRuntime.getRuntime() == MineScribeRuntime.APPLICATION) {
            return this.realCodec.encode(input, ops, prefix);
        } else {
            return this.jsonCodec.encode(input, ops, prefix);
        }
    }

    public static JsonCodec<Validation<?>> createJsonCodec() {
        return new JsonCodec<>(
                PretendValidation::new,
                validation -> {
                    if (validation instanceof IDataObject dataObject) {
                        return dataObject.getData();
                    } else {
                        return JsonNull.INSTANCE;
                    }
                }
        );
    }
}
