package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.util.List;
import java.util.UUID;

public record ListValueResponse(
        UUID requestedId,
        List<UUID> additionalLists,
        List<String> values
) {
    public static void write(ByteBuf byteBuf, ListValueResponse response) {
        NettyUtil.writeUUID(byteBuf, response.requestedId());
        NettyUtil.writeCollection(byteBuf, response.additionalLists(), NettyUtil::writeUUID);
        NettyUtil.writeCollection(byteBuf, response.values(), NettyUtil::writeUtf);
    }

    public static ListValueResponse read(ByteBuf byteBuf) {
        return new ListValueResponse(
                NettyUtil.readUUID(byteBuf),
                NettyUtil.readList(byteBuf, NettyUtil::readUUID),
                NettyUtil.readList(byteBuf, NettyUtil::readUtf)
        );
    }
}
