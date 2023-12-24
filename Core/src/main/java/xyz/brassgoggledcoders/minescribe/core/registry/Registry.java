package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.BiMapDispatchCodec;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.*;

public class Registry<K, V> implements Iterable<V> {
    private final String id;
    private final BiMap<K, V> values;
    private final Codec<V> dispatchCodec;

    public Registry(String id, Codec<K> kCodec) {
        this.id = id;
        this.values = HashBiMap.create();
        this.dispatchCodec = new BiMapDispatchCodec<>(
                this.id,
                kCodec,
                this::containsKey,
                this::getKey,
                this::getValue
        );
    }

    public boolean containsKey(K key) {
        return this.getMap().containsKey(key);
    }

    public boolean register(K key, V value) {
        if (this.values.containsKey(key)) {
            V oldValue = this.values.get(key);
            if (!oldValue.equals(value)) {
                this.values.put(key, value);
                return true;
            }
        } else {
            this.values.put(key, value);
            return true;
        }
        return false;
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

    public String getId() {
        return this.id;
    }

    public FancyText getName() {
        return FancyText.literal(this.getId());
    }

    public boolean isEmpty() {
        return this.getMap().isEmpty();
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return this.getMap()
                .values()
                .iterator();
    }

    public Collection<K> getKeys() {
        return this.getMap().keySet();
    }

    public boolean hasKey(K key) {
        return this.getMap().containsKey(key);
    }

    public K getKey(V value) {
        return this.getMap().inverse().get(value);
    }

    @SuppressWarnings("unused")
    public Optional<V> getOptionalValue(K key) {
        return Optional.ofNullable(this.getValue(key));
    }

    public void validate() {

    }
}
