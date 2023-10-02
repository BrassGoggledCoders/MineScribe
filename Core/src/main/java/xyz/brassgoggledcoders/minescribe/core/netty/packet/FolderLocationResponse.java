package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.nio.file.Path;

public record FolderLocationResponse(
        String folderType,
        Path folderPath
) {
    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeUtf(byteBuf, folderType);
        NettyUtil.writeUtf(byteBuf, folderPath.toString());
    }

    public static FolderLocationResponse decode(ByteBuf byteBuf) {
        return new FolderLocationResponse(
                NettyUtil.readUtf(byteBuf),
                Path.of(NettyUtil.readUtf(byteBuf))
        );
    }
}
