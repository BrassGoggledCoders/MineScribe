package xyz.brassgoggledcoders.minescribe.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.command.MineScribeCommand;

@EventBusSubscriber(modid = MineScribe.ID, value = Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher()
                .register(MineScribeCommand.create());
    }
}
