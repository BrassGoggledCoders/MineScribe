package xyz.brassgoggledcoders.minescribe.editor.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import javafx.event.Event;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyPacketDecoder;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyPacketHandler;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketRegistry;

import java.io.Closeable;
import java.util.Objects;
import java.util.function.Consumer;

public class MineScribeNettyServer extends Thread implements Closeable {
    private static MineScribeNettyServer INSTANCE;
    private static boolean addedShutDownHook = false;

    private final Consumer<Event> eventConsumer;
    private final int port;
    private ChannelFuture channelFuture;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelGroup channelGroup;

    public MineScribeNettyServer(Consumer<Event> eventConsumer, int port) {
        this.eventConsumer = eventConsumer;
        this.port = port;
    }

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(@NotNull SocketChannel ch) {
                            ch.pipeline().addLast(new MineScribeChannelHandler(
                                    MineScribeNettyServer.this::getChannelGroup,
                                    eventConsumer
                            ));
                            ch.pipeline().addLast(new NettyPacketDecoder());
                            ch.pipeline().addLast(new NettyPacketHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            this.channelFuture = b.bind("127.0.0.1", port).sync();
            this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            System.out.println("Started Server");
        } catch (InterruptedException e) {
            System.out.println("Failed to Create Server " + e.getMessage());
        }
    }

    private ChannelGroup getChannelGroup() {
        return this.channelGroup;
    }

    public void tryStart() {
        if (this.channelFuture == null || this.channelFuture.isDone()) {
            this.start();
        }
    }

    @Override
    public void close() {
        if (this.channelGroup != null) {
            this.channelGroup.close();
        }
        if (this.channelFuture != null) {
            this.channelFuture.channel().close();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
    }

    public void sendToClient(Object message) {
        ByteBuf byteBuf = Unpooled.buffer();
        PacketRegistry.INSTANCE.encodePacket(message, byteBuf);
        this.channelGroup.writeAndFlush(byteBuf);
    }

    public static void initialize(Consumer<Event> eventConsumer, int port) {
        if (INSTANCE != null && !INSTANCE.isAlive()) {
            INSTANCE = null;
        }
        if (INSTANCE == null) {
            INSTANCE = new MineScribeNettyServer(eventConsumer, port);
        }
        if (!addedShutDownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> INSTANCE.close()));
            addedShutDownHook = true;
        }
        INSTANCE.tryStart();
    }

    public static MineScribeNettyServer getInstance() {
        return Objects.requireNonNull(INSTANCE, "initialize has not been called");
    }
}
