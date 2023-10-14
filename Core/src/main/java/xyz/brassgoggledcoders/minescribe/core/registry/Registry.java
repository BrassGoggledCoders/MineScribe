package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.BiMapDispatchCodec;

import java.nio.file.Path;

public class Registry<K, V> {
    private final String name;
    private final BiMap<K, V> values;
    private final Codec<V> dispatchCodec;
    public Registry(String name, Codec<K> kCodec) {
        this.name = name;
        this.values = HashBiMap.create();
        this.dispatchCodec = new BiMapDispatchCodec<>(
                this.name,
                kCodec,
                this::getValues
        );
    }

    protected void register(K key, V value) {
        this.values.put(key, value);
    }

    public BiMap<K, V> getValues() {
        return values;
    }

    public Codec<V> getDispatchCodec() {
        return dispatchCodec;
    }

    protected String getName() {
        return this.name;
    }
}
