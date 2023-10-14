package xyz.brassgoggledcoders.minescribe.event;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.data.CodecMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.data.GameGatheredMineScribeReloadListener;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.FORGE)
public class ForgeCommonEventHandler {

    @SubscribeEvent
    public static void registerMineScribeResourceReloadListeners(RegisterMineScribeReloadListenerEvent event) {
        event.registerReloadListener(new GameGatheredMineScribeReloadListener<>(
                "registry",
                MineScribePackType.CODEC.listOf(),
                unused -> Path.of("packTypes.json"),
                () -> Stream.of(Arrays.stream(PackType.values())
                        .map(packType -> new MineScribePackType(
                                packType.name().toLowerCase(Locale.ROOT).replace("_", " "),
                                packType.name(),
                                Path.of(packType.getDirectory()),
                                packType.getVersion(SharedConstants.getCurrentVersion()),
                                Optional.of("forge:%s_pack_format".formatted(packType.bridgeType.name().toLowerCase(Locale.ROOT)))
                        ))
                        .toList()
                )
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/parent",
                PackContentParentType.CODEC
        ));
        //event.registerReloadListener(new PackContentSubTypeManager());
    }
}
