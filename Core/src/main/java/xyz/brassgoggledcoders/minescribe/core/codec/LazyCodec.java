package xyz.brassgoggledcoders.minescribe.core.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Supplier;

public record LazyCodec<A>(Supplier<Codec<A>> delegate) implements Codec<A> {
    public LazyCodec {
        delegate = Suppliers.memoize(delegate::get);
    }

    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return this.delegate.get().decode(ops, input);
    }

    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return this.delegate.get().encode(input, ops, prefix);
    }

    public static <T> LazyCodec<T> of(Supplier<Codec<T>> codec) {
        return new LazyCodec<>(codec);
    }
}
