package xyz.brassgoggledcoders.minescribe.util;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class PackTypeHelper {
    public static Stream<MineScribePackType> gatherPackTypes() {
        return Arrays.stream(PackType.values())
                .map(packType -> new MineScribePackType(
                        FancyText.literal(packType.getSerializedName()),
                        packType.name(),
                        Path.of(packType.getDirectory()),
                        SharedConstants.getCurrentVersion()
                                .getPackVersion(packType),
                        Optional.empty()
                ));
    }
}
