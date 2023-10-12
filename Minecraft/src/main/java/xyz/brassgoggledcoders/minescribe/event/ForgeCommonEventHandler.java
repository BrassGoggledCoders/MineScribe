package xyz.brassgoggledcoders.minescribe.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;
import xyz.brassgoggledcoders.minescribe.data.PackContentSubTypeManager;
import xyz.brassgoggledcoders.minescribe.data.PackContentTypeManager;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.FORGE)
public class ForgeCommonEventHandler {

    @SubscribeEvent
    public static void registerMineScribeResourceReloadListeners(RegisterMineScribeReloadListenerEvent event) {
        event.registerReloadListener(new PackContentTypeManager());
        event.registerReloadListener(new PackContentSubTypeManager());
    }
}
