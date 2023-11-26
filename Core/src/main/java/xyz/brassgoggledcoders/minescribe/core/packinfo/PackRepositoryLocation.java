package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;

public record PackRepositoryLocation(
        String label,
        String pathMatcher
) implements ILabeledValue {
    public static final Codec<PackRepositoryLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(PackRepositoryLocation::label),
            Codec.STRING.fieldOf("path_matcher").forGetter(PackRepositoryLocation::pathMatcher)
    ).apply(instance, PackRepositoryLocation::new));

    @Override
    public String getLabel() {
        return this.label;
    }
}
