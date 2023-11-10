package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.KeyDispatchCodec;

import java.util.function.Function;

public class MappedKeyDispatchCodec<K, V> extends KeyDispatchCodec<K, V> {

    public MappedKeyDispatchCodec(
            final String typeKey,
            final Codec<K> keyCodec,
            final Function<? super V, ? extends DataResult<? extends K>> type,
            final Function<? super K, ? extends DataResult<? extends Codec<? extends V>>> codec) {
        super(typeKey, keyCodec, type, codec, v -> getCodec(type, codec, v), true);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> DataResult<? extends Encoder<V>> getCodec(final Function<? super V, ? extends DataResult<? extends K>> type, final Function<? super K, ? extends DataResult<? extends Encoder<? extends V>>> encoder, final V input) {
        return type.apply(input).<Encoder<? extends V>>flatMap(k -> encoder.apply(k).map(Function.identity())).map(c -> ((Encoder<V>) c));
    }

    public static <A, E> Codec<E> dispatch(Codec<A> start, final Function<? super E, ? extends A> type, final Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatch(start, "type", type, codec);
    }

    public static <A, E> Codec<E> dispatch(Codec<A> start, final String typeKey, final Function<? super E, ? extends A> type, final Function<? super A, ? extends Codec<? extends E>> codec) {
        return partialDispatch(start, typeKey, type.andThen(DataResult::success), codec.andThen(DataResult::success));
    }

    public static <A, E> Codec<E> partialDispatch(Codec<A> start, final String typeKey, final Function<? super E, ? extends DataResult<? extends A>> type, final Function<? super A, ? extends DataResult<? extends Codec<? extends E>>> codec) {
        return new MappedKeyDispatchCodec<>(typeKey, start, type, codec).codec();
    }
}
