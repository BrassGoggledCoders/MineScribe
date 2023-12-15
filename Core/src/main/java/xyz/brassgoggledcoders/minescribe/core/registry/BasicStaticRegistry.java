package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BasicStaticRegistry<K, V> extends Registry<K, V> {
    public BasicStaticRegistry(String name, Codec<K> kCodec, Consumer<BiConsumer<K, V>> initialize) {
        super(name, kCodec);
        initialize.accept(this.getMap()::put);
    }


    @Override
    public boolean containsKey(K key) {
        return super.containsKey(this.alterKey(key));
    }

    @Override
    public V getValue(K key) {
        return super.getValue(this.alterKey(key));
    }

    @SuppressWarnings("unchecked")
    private K alterKey(K key) {
        if (key instanceof String stringKey) {
            if (stringKey.startsWith("minescribe:")) {
                key = (K) stringKey.split(":")[1];
            }
        }
        return key;
    }
}
