package xyz.brassgoggledcoders.minescribe;


import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
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

    }
}
