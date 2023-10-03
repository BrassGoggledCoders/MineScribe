package xyz.brassgoggledcoders.minescribe.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataRequest;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PacketRegistry {
    public static final PacketRegistry INSTANCE = new PacketRegistry();

    private final List<PacketWrapper<?>> packets;
    private final Map<Class<?>, PacketHandler<?>> packetHandlers;

    public PacketRegistry() {
        this.packets = new ArrayList<>();
        this.packetHandlers = new HashMap<>();

        this.packets.add(new PacketWrapper<>(
                InstanceDataRequest.class,
                InstanceDataRequest::new
        ));
        this.packets.add(new PacketWrapper<>(
                InstanceDataResponse.class,
                InstanceDataResponse::decode,
                InstanceDataResponse::encode
        ));
    }

    public Object decodePacket(ByteBuf byteBuf) {
        PacketWrapper<?> packetWrapper = this.packets.get(byteBuf.readInt());

        if (packetWrapper != null) {
            return packetWrapper.decoder().apply(byteBuf);
        } else {
            throw new DecoderException("Failed to Find Packet for ByteBuf");
        }
    }

    public void encodePacket(Object object, ByteBuf byteBuf) {
        PacketWrapper<?> packetWrapper = null;
        int checkingId = -1;
        while (packetWrapper == null && ++checkingId < packets.size()) {
            final PacketWrapper<?> checkingWrapper = packets.get(checkingId);
            if (checkingWrapper.clazz() == object.getClass()) {
                packetWrapper = checkingWrapper;
            }
        }

        if (packetWrapper != null) {
            byteBuf.writeInt(checkingId);
            //noinspection unchecked
            encode(object, (PacketWrapper<Object>) packetWrapper, byteBuf);
        }
    }

    private void encode(Object o, PacketWrapper<Object> packetWrapper, ByteBuf byteBuf) {
        packetWrapper.encoder().accept(o, byteBuf);
    }

    public <T> void addPacketHandler(Class<T> tClass, Consumer<T> handler) {
        this.packetHandlers.put(tClass, new PacketHandler<>(tClass, handler));
    }

    public void handlePacket(Object object) {
        PacketHandler<?> packetHandler = packetHandlers.get(object.getClass());
        if (packetHandler != null) {
            packetHandler.handleObject(object);
        }
    }

    public void decodeAndHandle(ByteBuf byteBuf) {
        this.handlePacket(this.decodePacket(byteBuf));
    }
}
