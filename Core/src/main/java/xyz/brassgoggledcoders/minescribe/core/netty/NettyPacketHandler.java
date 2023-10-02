package xyz.brassgoggledcoders.minescribe.core.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class NettyPacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        PacketRegistry.INSTANCE.handlePacket(msg);
    }
}
