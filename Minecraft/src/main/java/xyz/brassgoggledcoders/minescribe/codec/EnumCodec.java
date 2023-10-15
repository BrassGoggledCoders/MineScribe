package xyz.brassgoggledcoders.minescribe.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final Class<E> enumClass;

    public EnumCodec(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getStringValue(input)
                .flatMap(name -> {
                    for (E value: this.enumClass.getEnumConstants()) {
                        if (name.equals(value.name())) {
                            return DataResult.success(Pair.of(value, input));
                        }
                    }
                    return DataResult.error("No Enum found for %s".formatted(name));
                });
    }

    @Override
    public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
        return DataResult.success(ops.createString(input.name()));
    }
}
