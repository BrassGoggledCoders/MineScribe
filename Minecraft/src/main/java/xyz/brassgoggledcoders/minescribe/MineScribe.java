package xyz.brassgoggledcoders.minescribe;


import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.connection.MineScribeNettyClient;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketRegistry;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataRequest;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataResponse;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod(MineScribe.ID)
public class MineScribe {
    public static final String ID = "minescribe";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public MineScribe() {
        PacketRegistry.INSTANCE.addPacketHandler(
                InstanceDataRequest.class,
                instanceDataRequest -> {
                    Map<String, Path> packLocations = new HashMap<>();
                    packLocations.put(
                            "Client Resource Packs",
                            Minecraft.getInstance().getResourcePackDirectory().getAbsoluteFile().toPath()
                    );
                    Optional.ofNullable(Minecraft.getInstance().getSingleplayerServer())
                            .ifPresent(minecraftServer -> packLocations.put(
                                    "Level Data Packs",
                                    minecraftServer.getFile("datapacks").getAbsoluteFile().toPath()
                            ));

                    MineScribeNettyClient.getInstance().sendToClient(new InstanceDataResponse(
                            PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()),
                            PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()),
                            packLocations
                    ));
                }
        );
    }
}
