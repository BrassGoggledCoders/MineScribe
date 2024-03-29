package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BasicStaticRegistry<K, V> extends Registry<K, V> {
    public BasicStaticRegistry(String name, Codec<K> kCodec, Consumer<BiConsumer<K, V>> initialize) {
        super(name, kCodec, null);
        initialize.accept(this::register);
    }
}
