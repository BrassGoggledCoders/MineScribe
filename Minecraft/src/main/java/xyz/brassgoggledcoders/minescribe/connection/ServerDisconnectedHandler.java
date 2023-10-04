package xyz.brassgoggledcoders.minescribe.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class ServerDisconnectedHandler extends ChannelInboundHandlerAdapter {
    private final Runnable onServerClosed;

    public ServerDisconnectedHandler(Runnable onServerClosed) {
        this.onServerClosed = onServerClosed;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.onServerClosed.run();
    }
}
