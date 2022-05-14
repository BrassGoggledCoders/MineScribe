package xyz.brassgoggledcoders.minescribe;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.brassgoggledcoders.minescribe.command.MineScribeCommand;
import xyz.brassgoggledcoders.minescribe.config.ServerConfig;

@Mod(MineScribe.ID)
public class MineScribe {
    public static final String ID = "minescribe";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    public static ServerConfig SERVER_CONFIG;

    public MineScribe() {
        ModLoadingContext.get().registerExtensionPoint(
                DisplayTest.class,
                () -> new DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true
                )
        );

        Pair<ServerConfig, ForgeConfigSpec> specs = ServerConfig.setup();
        SERVER_CONFIG = specs.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, specs.getRight());

        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
    }

    private void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(MineScribeCommand.create());
    }
}
