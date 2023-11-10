package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;
import java.util.function.Predicate;

public class BiMapDispatchCodec<K, V> implements Codec<V> {

    private final String name;
    private final Codec<K> keyCodec;
    private final Predicate<K> keyExists;
    private final Function<V, K> getKey;
    private final Function<K, V> getValue;

    public BiMapDispatchCodec(String name, Codec<K> keyCodec, Predicate<K> keyExists, Function<V, K> getKey, Function<K, V> getValue) {
        this.name = name;
        this.keyCodec = keyCodec;
        this.keyExists = keyExists;
        this.getKey = getKey;
        this.getValue = getValue;
    }

    @Override
    public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
        return keyCodec.decode(ops, input)
                .flatMap(keyValuePair -> {
                    if (this.keyExists.test(keyValuePair.getFirst())) {
                        return DataResult.success(keyValuePair.mapFirst(this.getValue));
                    } else {
                        return DataResult.error("Unknown registry key in " + this.name + ": " + keyValuePair.getFirst());
                    }
                });
    }

    @Override
    public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
        K key = this.getKey.apply(input);
        if (key == null) {
            return DataResult.error("Unknown registry element in " + this.name + ": " + input);
        }
        T toMerge = ops.createString(key.toString());
        return ops.mergeToPrimitive(prefix, toMerge);
    }
}