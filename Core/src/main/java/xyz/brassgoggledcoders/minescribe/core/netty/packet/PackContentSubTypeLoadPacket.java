package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record PackContentSubTypeLoadPacket(
        Map<ResourceId, Collection<PackContentType>> packContentSubTypes
) {

    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeMap(
                byteBuf,
                this.packContentSubTypes(),
                (keyByteBuf, key) -> key.encode(keyByteBuf),
                (valueByteBuf, value) -> NettyUtil.writeCollection(
                        valueByteBuf,
                        value,
                        (listByteBuf, listValue)-> listValue.encode(listByteBuf)
                )
        );
    }

    public static PackContentSubTypeLoadPacket decode(ByteBuf byteBuf) {
        return new PackContentSubTypeLoadPacket(
                NettyUtil.readMap(
                        byteBuf,
                        ResourceId::decode,
                        valueByteBuf -> NettyUtil.readList(
                                valueByteBuf,
                                PackContentType::decode
                        )
                )
        );
    }
}
