package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.nio.file.Path;
import java.util.Map;

public record InstanceDataResponse(
        int resourcePackVersion,
        int dataPackVersion,
        Map<String, Path> packFolders
) {
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(this.resourcePackVersion());
        byteBuf.writeInt(this.dataPackVersion());
        NettyUtil.writeMap(
                byteBuf,
                this.packFolders(),
                NettyUtil::writeUtf,
                (valueByteBuf, path) -> NettyUtil.writeUtf(valueByteBuf, path.toString())
        );
    }

    public static InstanceDataResponse decode(ByteBuf byteBuf) {
        return new InstanceDataResponse(
                byteBuf.readInt(),
                byteBuf.readInt(),
                NettyUtil.readMap(
                        byteBuf,
                        NettyUtil::readUtf,
                        valueByteBuf -> Path.of(NettyUtil.readUtf(valueByteBuf))
                )
        );
    }
}
