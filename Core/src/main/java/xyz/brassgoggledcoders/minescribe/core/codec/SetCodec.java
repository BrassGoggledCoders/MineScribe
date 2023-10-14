package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SetCodec<A> implements Codec<Set<A>> {
    private final Codec<A> elementCodec;

    public SetCodec(final Codec<A> elementCodec) {
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<T> encode(final Set<A> input, final DynamicOps<T> ops, final T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();

        for (final A a : input) {
            builder.add(elementCodec.encodeStart(ops, a));
        }

        return builder.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<Set<A>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getStream(input)
                .flatMap(stream -> stream.map(value -> this.elementCodec.decode(ops, value))
                        .map(result -> result.map(Pair::getFirst))
                        .collect(new DataResultCollector<>(HashSet::new))
                        .map(success -> Pair.of(success, input))
                );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SetCodec<?> setCodec = (SetCodec<?>) o;
        return Objects.equals(elementCodec, setCodec.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementCodec);
    }

    @Override
    public String toString() {
        return "SetCodec[" + elementCodec + ']';
    }
}
