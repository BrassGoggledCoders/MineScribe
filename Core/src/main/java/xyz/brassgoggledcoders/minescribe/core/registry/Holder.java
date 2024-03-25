package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.datafixers.types.Func;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Holder<K, V> implements Supplier<V> {
    private final K key;

    private V value;

    public Holder(K key) {
        this.key = key;
    }

    public void bind(V value) {
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    @Override
    @Nullable
    public V get() {
        return this.value;
    }

    public Optional<V> getOpt() {
        return Optional.ofNullable(this.get());
    }

    public <F> F fold(Function<V, F> ifPresent, Supplier<F> ifAbsent) {
        V value = this.get();
        if (value != null) {
            return ifPresent.apply(value);
        } else {
            return ifAbsent.get();
        }
    }

    public Stream<V> stream() {
        return Stream.ofNullable(this.get());
    }

    public boolean exists(Predicate<V> vPredicate) {
        return this.getOpt()
                .filter(vPredicate)
                .isPresent();
    }
}
