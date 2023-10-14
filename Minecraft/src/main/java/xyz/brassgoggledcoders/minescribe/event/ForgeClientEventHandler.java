package xyz.brassgoggledcoders.minescribe.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.event.GatherPackRepositoryLocationsEvent;
import xyz.brassgoggledcoders.minescribe.command.MineScribeCommand;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;

import java.util.Optional;

@EventBusSubscriber(modid = MineScribe.ID, value = Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher()
                .register(MineScribeCommand.create());
    }
    @SubscribeEvent
    public static void registerPackRepositoryLocations(GatherPackRepositoryLocationsEvent packRepositoryLocations) {
        packRepositoryLocations.register(new PackRepositoryLocation(
                "Client Resource Packs",
                Minecraft.getInstance()
                        .getResourcePackDirectory()
                        .getAbsoluteFile()
                        .toPath()
        ));
        Optional.ofNullable(Minecraft.getInstance().getSingleplayerServer())
                .ifPresent(minecraftServer -> packRepositoryLocations.register(new PackRepositoryLocation(
                        "Level Data Packs",
                        minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath()
                )));
    }
}
