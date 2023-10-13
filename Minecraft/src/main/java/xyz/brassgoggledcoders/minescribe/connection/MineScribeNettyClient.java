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
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.MineScribeInfo;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyPacketDecoder;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyPacketHandler;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketHandler;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketRegistry;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataRequest;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataResponse;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackTypeInfo;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class MineScribeNettyClient implements AutoCloseable {
    private static MineScribeNettyClient INSTANCE;
    private static boolean addedShutDownHook = false;

    private final int port;

    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;

    public MineScribeNettyClient(int port) {
        this.port = port;
    }

    public void start() {
        PacketRegistry.INSTANCE.setup(this::registerHandlers);
        this.workerGroup = new NioEventLoopGroup();
        this.channelFuture = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(@NotNull SocketChannel ch) {
                        ch.pipeline().addLast(new ServerDisconnectedHandler(MineScribeNettyClient.INSTANCE::close));
                        ch.pipeline().addLast(new NettyPacketDecoder());
                        ch.pipeline().addLast(new NettyPacketHandler());
                    }
                })
                .connect(NetUtil.LOCALHOST4, port);
    }

    public void sendToClient(Object message) {
        ByteBuf byteBuf = Unpooled.buffer();
        PacketRegistry.INSTANCE.encodePacket(message, byteBuf);
        this.channelFuture.channel().writeAndFlush(byteBuf);
    }

    public void tryStart() {
        if (this.channelFuture == null) {
            this.start();
        }
    }

    @Override
    public void close() {
        if (this.channelFuture != null) {
            this.channelFuture.channel().close();
            this.channelFuture = null;
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
            this.workerGroup = null;
        }
    }

    private void registerHandlers(Consumer<PacketHandler<?>> packetHandlerConsumer) {
        packetHandlerConsumer.accept(new PacketHandler<>(
                InstanceDataRequest.class,
                instanceDataRequest -> {
                    Map<String, Path> packRepositories = new HashMap<>();
                    packRepositories.put(
                            "Client Resource Packs",
                            Minecraft.getInstance().getResourcePackDirectory().getAbsoluteFile().toPath()
                    );
                    Optional.ofNullable(Minecraft.getInstance().getSingleplayerServer())
                            .ifPresent(minecraftServer -> packRepositories.put(
                                    "Level Data Packs",
                                    minecraftServer.getFile("datapacks").getAbsoluteFile().toPath()
                            ));

                    List<PackTypeInfo> packTypeInfos = Arrays.stream(PackType.values())
                            .map(packType -> new PackTypeInfo(
                                    packType.name().toLowerCase(Locale.ROOT).replace("_", " "),
                                    packType.name(),
                                    Path.of(packType.getDirectory()),
                                    packType.getVersion(SharedConstants.getCurrentVersion()),
                                    "forge:%s_pack_format".formatted(packType.bridgeType.name().toLowerCase(Locale.ROOT))
                            ))
                            .toList();

                    MineScribeNettyClient.getInstance().sendToClient(new InstanceDataResponse(
                            packTypeInfos,
                            packRepositories
                    ));
                }
        ));
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
