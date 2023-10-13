package xyz.brassgoggledcoders.minescribe.core.packinfo;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

public record ResourceId(
        String namespace,
        String path
) {

    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeUtf(byteBuf, this.namespace());
        NettyUtil.writeUtf(byteBuf, this.path());
    }

    public static ResourceId decode(ByteBuf byteBuf) {
        return new ResourceId(
                NettyUtil.readUtf(byteBuf),
                NettyUtil.readUtf(byteBuf)
        );
    }

}
