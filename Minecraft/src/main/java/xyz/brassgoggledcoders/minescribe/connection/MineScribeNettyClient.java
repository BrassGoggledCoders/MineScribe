package xyz.brassgoggledcoders.minescribe.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.NetUtil;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.MineScribeInfo;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyPacketDecoder;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyPacketHandler;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketRegistry;

public class MineScribeNettyClient extends Thread implements AutoCloseable {
    private static MineScribeNettyClient INSTANCE;
    private static boolean addedShutDownHook = false;

    private final int port;

    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;

    public MineScribeNettyClient(int port) {
        this.port = port;
        this.setName("MineScribe Netty Client Thread");
    }

    public void run() {
        workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(@NotNull SocketChannel ch) {
                ch.pipeline().addLast(new NettyPacketDecoder());
                ch.pipeline().addLast(new NettyPacketHandler());
            }
        });

        this.channelFuture = b.connect(NetUtil.LOCALHOST4, port);
    }

    public void sendToClient(Object message) {
        ByteBuf byteBuf = Unpooled.buffer();
        PacketRegistry.INSTANCE.encodePacket(message, byteBuf);
        this.channelFuture.channel().writeAndFlush(byteBuf);
    }

    public void tryStart() {
        if (this.channelFuture == null || this.channelFuture.isDone()) {
            this.start();
        }
    }

    @Override
    public void close() {
        if (this.channelFuture != null) {
            this.channelFuture.channel().close();
        }
        if (this.workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public static MineScribeNettyClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MineScribeNettyClient(MineScribeInfo.DEFAULT_PORT);
        }
        if (!addedShutDownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> MineScribeNettyClient.INSTANCE.close()));
            addedShutDownHook = true;
        }
        return INSTANCE;
    }
}
