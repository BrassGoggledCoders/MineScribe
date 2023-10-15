package xyz.brassgoggledcoders.minescribe.core.codec;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Supplier;

public class BiMapDispatchCodec<K, V> implements Codec<V> {

    private final String name;
    private final Codec<K> keyCodec;
    private final Supplier<BiMap<K, V>> getMap;

    public BiMapDispatchCodec(String name, Codec<K> keyCodec, Supplier<BiMap<K, V>> getMap) {
        this.name = name;
        this.keyCodec = keyCodec;
        this.getMap = getMap;
    }

    @Override
    public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
        return keyCodec.decode(ops, input)
                .flatMap(keyValuePair -> {
                    if (this.getMap.get().containsKey(keyValuePair.getFirst())) {
                        return DataResult.success(keyValuePair.mapFirst(this.getMap.get()::get));
                    } else {
                        return DataResult.error("Unknown registry key in " + this.name + ": " + keyValuePair.getFirst());
                    }
                });
    }

    @Override
    public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
        K key = this.getMap.get().inverse().get(input);
        if (key == null) {
            return DataResult.error("Unknown registry element in " + this.name + ": " + input);
        }
        T toMerge = ops.createString(key.toString());
        return ops.mergeToPrimitive(prefix, toMerge);
    }
}