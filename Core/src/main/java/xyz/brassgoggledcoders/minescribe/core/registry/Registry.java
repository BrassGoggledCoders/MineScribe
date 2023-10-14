package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.BiMapDispatchCodec;

import java.util.ArrayList;
import java.util.List;

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
                this::getMap
        );
    }

    protected void register(K key, V value) {
        this.values.put(key, value);
    }

    public BiMap<K, V> getMap() {
        return values;
    }

    public V getValue(K key) {
        return this.getMap().get(key);
    }

    public List<V> getValues() {
        return new ArrayList<>(this.getMap().values());
    }

    public Codec<V> getCodec() {
        return dispatchCodec;
    }

    protected String getName() {
        return this.name;
    }

    public boolean isEmpty() {
        return this.getMap().isEmpty();
    }
}
