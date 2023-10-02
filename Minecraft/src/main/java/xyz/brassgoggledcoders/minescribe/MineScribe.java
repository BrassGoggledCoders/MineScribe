package xyz.brassgoggledcoders.minescribe;


import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.connection.MineScribeNettyClient;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketRegistry;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.FolderLocationRequest;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.FolderLocationResponse;

import java.util.Optional;

@Mod(MineScribe.ID)
public class MineScribe {
    public static final String ID = "minescribe";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public MineScribe() {
        PacketRegistry.INSTANCE.addPacketHandler(
                FolderLocationRequest.class,
                folderLocationRequest -> {
                    MineScribeNettyClient.getInstance().sendToClient(new FolderLocationResponse(
                            "resource pack",
                            Minecraft.getInstance().getResourcePackDirectory().toPath()
                    ));
                    Optional.ofNullable(Minecraft.getInstance().level)
                            .map(Level::getServer)
                            .ifPresent(minecraftServer -> MineScribeNettyClient.getInstance()
                                    .sendToClient(new FolderLocationResponse(
                                            "data pack",
                                            minecraftServer.getFile("datapacks").toPath()
                                    ))
                            );
                }
        );
    }
}
