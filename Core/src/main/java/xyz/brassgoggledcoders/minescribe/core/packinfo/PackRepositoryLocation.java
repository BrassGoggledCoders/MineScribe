package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;

import java.nio.file.Path;

public record PackRepositoryLocation(
        String label,
        Path path
) {
    public static final Codec<PackRepositoryLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(PackRepositoryLocation::label),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackRepositoryLocation::path)
    ).apply(instance, PackRepositoryLocation::new));
}
