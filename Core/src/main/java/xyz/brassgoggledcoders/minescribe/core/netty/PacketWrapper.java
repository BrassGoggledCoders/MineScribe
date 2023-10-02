package xyz.brassgoggledcoders.minescribe.core.netty;

import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record PacketWrapper<T>(
        Class<T> clazz,
        Function<ByteBuf, T> decoder,
        BiConsumer<T, ByteBuf> encoder
) {

    public PacketWrapper(
            Class<T> clazz,
            Supplier<T> creator
    ) {
        this(
                clazz,
                byteBuf -> creator.get(),
                (t, byteBuf) -> {
                }
        );
    }
}
