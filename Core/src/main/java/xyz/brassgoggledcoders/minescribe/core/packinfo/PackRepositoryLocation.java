package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

public record PackRepositoryLocation(
        FancyText label,
        String pathMatcher
) implements ILabeledValue {
    public static final Codec<PackRepositoryLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(PackRepositoryLocation::label),
            Codec.STRING.fieldOf("path_matcher").forGetter(PackRepositoryLocation::pathMatcher)
    ).apply(instance, PackRepositoryLocation::new));

    @Override
    public FancyText getLabel() {
        return this.label;
    }
}
