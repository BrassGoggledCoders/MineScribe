package xyz.brassgoggledcoders.minescribe.core.packinfo;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.nio.file.Path;

public record PackContentType(
        ResourceId resourceId,
        String localization,
        String packType,
        Path path
) {

    public void encode(ByteBuf byteBuf) {
        this.resourceId().encode(byteBuf);
        NettyUtil.writeUtf(byteBuf, this.localization());
        NettyUtil.writeUtf(byteBuf, this.packType());
        NettyUtil.writeUtf(byteBuf, this.path().toString());
    }

    public static PackContentType decode(ByteBuf byteBuf) {
        return new PackContentType(
                ResourceId.decode(byteBuf),
                NettyUtil.readUtf(byteBuf),
                NettyUtil.readUtf(byteBuf),
                Path.of(NettyUtil.readUtf(byteBuf))
        );
    }

}
