package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;

import java.util.List;

public record PackContentTypeLoadPacket(
        List<PackContentType> packContentTypes
) {

    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeCollection(byteBuf, this.packContentTypes(), (listByteBut, value) -> value.encode(listByteBut));
    }

    public static PackContentTypeLoadPacket decode(ByteBuf byteBuf) {
        return new PackContentTypeLoadPacket(
                NettyUtil.readList(byteBuf, PackContentType::decode)
        );
    }
}
