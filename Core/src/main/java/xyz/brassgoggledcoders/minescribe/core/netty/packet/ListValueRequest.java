package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.util.UUID;

public record ListValueRequest(
        UUID listId
) {

    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeUUID(byteBuf, this.listId());
    }

    public static ListValueRequest decode(ByteBuf byteBuf) {
        return new ListValueRequest(NettyUtil.readUUID(byteBuf));
    }
}
