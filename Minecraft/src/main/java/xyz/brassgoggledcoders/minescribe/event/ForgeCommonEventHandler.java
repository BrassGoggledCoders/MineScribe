package xyz.brassgoggledcoders.minescribe.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.data.CodecMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.data.GameGatheredMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.util.PackTypeHelper;

import java.nio.file.Path;
import java.util.stream.Stream;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.FORGE)
public class ForgeCommonEventHandler {

    @SubscribeEvent
    public static void registerMineScribeResourceReloadListeners(RegisterMineScribeReloadListenerEvent event) {
        event.registerReloadListener(new GameGatheredMineScribeReloadListener<>(
                "registry",
                MineScribePackType.CODEC.listOf(),
                unused -> Path.of("packTypes.json"),
                () -> Stream.of(PackTypeHelper.gatherPackTypes()
                        .toList()
                )
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/parent",
                "registry/types/parent",
                MineScribeCodecs.PACK_CONTENT_PARENT_TYPE,
                PackContentParentType.CODEC,
                true
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/child",
                "registry/types/child",
                MineScribeCodecs.PACK_CONTENT_CHILD_TYPE,
                PackContentChildType.CODEC,
                true
        ));
    }
}
