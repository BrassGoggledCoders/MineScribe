package xyz.brassgoggledcoders.minescribe.core.packinfo;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.nio.file.Path;
import java.util.Optional;

public record PackContentType(
        ResourceId resourceId,
        String localization,
        String packType,
        Path path,
        Optional<FileForm> form
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
                Path.of(NettyUtil.readUtf(byteBuf)),
                Optional.empty()
        );
    }

}
