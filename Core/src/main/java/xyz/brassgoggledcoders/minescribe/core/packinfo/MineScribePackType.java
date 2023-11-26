package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeStringHelper;

import java.nio.file.Path;
import java.util.Optional;

public record MineScribePackType(
        String label,
        String name,
        Path folder,
        int version,
        Optional<String> versionKey
) implements ILabeledValue {
    public static final Codec<MineScribePackType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(MineScribePackType::label),
            Codec.STRING.fieldOf("name").forGetter(MineScribePackType::name),
            Codec.STRING.fieldOf("path").xmap(Path::of, Path::toString).forGetter(MineScribePackType::folder),
            Codec.INT.fieldOf("version").forGetter(MineScribePackType::version),
            Codec.STRING.optionalFieldOf("versionKey").forGetter(MineScribePackType::versionKey)
    ).apply(instance, MineScribePackType::new));

    @Override
    public String toString() {
        return "%s (./%s)".formatted(MineScribeStringHelper.toTitleCase(this.label()), folder.toString());
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
