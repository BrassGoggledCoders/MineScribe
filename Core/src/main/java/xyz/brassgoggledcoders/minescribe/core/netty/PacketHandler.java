package xyz.brassgoggledcoders.minescribe.core.netty;

import java.util.function.Consumer;

public record PacketHandler<T>(
        Class<T> tClass,
        Consumer<T> consumer
) {
    public void handleObject(Object o) {
        if (tClass.isInstance(o)) {
            consumer.accept(tClass.cast(o));
        }
    }
}
