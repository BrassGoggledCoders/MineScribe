package xyz.brassgoggledcoders.minescribe.editor.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import javafx.event.Event;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent.ClientConnectionNetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent.ConnectionStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MineScribeChannelHandler extends ChannelInboundHandlerAdapter {
    private final Supplier<ChannelGroup> channelGroup;
    private final Consumer<Event> eventConsumer;

    public MineScribeChannelHandler(Supplier<ChannelGroup> channelGroup, Consumer<Event> eventConsumer) {
        this.channelGroup = channelGroup;
        this.eventConsumer = eventConsumer;
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelGroup.get().add(ctx.channel());
        this.eventConsumer.accept(new ClientConnectionNetworkEvent(ConnectionStatus.CONNECTED));
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelGroup.get().remove(ctx.channel());
        this.eventConsumer.accept(new ClientConnectionNetworkEvent(ConnectionStatus.DISCONNECTED));
    }
}
