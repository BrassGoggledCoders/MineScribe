package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.OptionalFieldCodec;

import java.util.Optional;

public class ErroringOptionalFieldCodec<A> extends OptionalFieldCodec<A> {
    private final String name;
    private final Codec<A> elementCodec;
    public ErroringOptionalFieldCodec(String name, Codec<A> elementCodec) {
        super(name, elementCodec);
        this.name = name;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Optional<A>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) {
            return DataResult.success(Optional.empty());
        } else {
            return elementCodec.parse(ops, value)
                    .map(Optional::of);
        }
    }

    public static <B> ErroringOptionalFieldCodec<B> of(String name, Codec<B> codec) {
        return new ErroringOptionalFieldCodec<>(name, codec);
    }
}
