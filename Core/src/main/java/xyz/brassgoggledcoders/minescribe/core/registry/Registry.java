package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.codec.BiMapDispatchCodec;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Registry<K, V> implements Iterable<V> {
    private final String id;
    private final Map<K, Holder<K, V>> byKey;
    private final Map<V, Holder<K, V>> byValue;
    private final Codec<V> dispatchCodec;
    private final Function<V, String> aliasFunction;

    public Registry(String id, Codec<K> kCodec, Function<V, String> aliasFunction) {
        this.id = id;
        this.aliasFunction = Objects.requireNonNullElseGet(aliasFunction, () -> value -> null);
        this.byKey = new HashMap<>();
        this.byValue = new HashMap<>();
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
        Holder<K, V> holder = this.byKey.computeIfAbsent(key, Holder::new);
        V oldValue = holder.get();
        if (oldValue != null) {
            this.byValue.remove(oldValue);
        }
        holder.bind(value);
        this.byValue.put(value, holder);
        return true;
    }

    public Map<K, Holder<K, V>> getMap() {
        return byKey;
    }

    public V getValue(K key) {
        Holder<K, V> holder = this.getMap().get(key);
        if (holder != null) {
            return holder.get();
        } else {
            return null;
        }
    }

    public List<V> getValues() {
        return this.byKey.values()
                .stream()
                .map(Holder::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
        return this.getValues()
                .iterator();
    }

    @Nullable
    public String getAlias(V value) {
        return this.aliasFunction.apply(value);
    }

    public Collection<K> getKeys() {
        return this.getMap().keySet();
    }

    public boolean hasKey(K key) {
        return this.getMap().containsKey(key);
    }

    public K getKey(V value) {
        Holder<K, V> holder = this.byValue.get(value);
        if (holder != null) {
            return holder.getKey();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public Optional<V> getOptionalValue(K key) {
        return Optional.ofNullable(this.getValue(key));
    }

    public void validate() {

    }

    public Collection<Holder<K, V>> getHolders() {
        return this.byKey.values();
    }
}
