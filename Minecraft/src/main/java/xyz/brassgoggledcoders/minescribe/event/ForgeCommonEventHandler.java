package xyz.brassgoggledcoders.minescribe.event;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.command.MineScribeCommand;

@EventBusSubscriber(modid = MineScribe.ID)
public class ForgeCommonEventHandler {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher()
                .register(MineScribeCommand.create());
    }
}
