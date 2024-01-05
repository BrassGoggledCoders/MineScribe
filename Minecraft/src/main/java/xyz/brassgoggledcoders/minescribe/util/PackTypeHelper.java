package xyz.brassgoggledcoders.minescribe.util;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeStringHelper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class PackTypeHelper {
    public static Stream<MineScribePackType> gatherPackTypes() {
        return Arrays.stream(PackType.values())
                .map(packType -> new MineScribePackType(
                        FancyText.literal(MineScribeStringHelper.toTitleCase(packType.name()
                                .toLowerCase(Locale.ROOT)
                                .replace("_", " ")
                        )),
                        packType.name(),
                        Path.of(packType.getDirectory()),
                        packType.getVersion(SharedConstants.getCurrentVersion()),
                        Optional.of("forge:%s_pack_format".formatted(packType.bridgeType.name().toLowerCase(Locale.ROOT)))
                ));
    }
}
