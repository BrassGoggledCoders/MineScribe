package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeStringHelper;

import java.nio.file.Path;
import java.util.Optional;

public record MineScribePackType(
        FancyText label,
        String name,
        Path folder,
        int version,
        Optional<String> versionKey
) implements ILabeledValue {
    public static final Codec<MineScribePackType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(MineScribePackType::label),
            Codec.STRING.fieldOf(JsonFieldNames.NAME).forGetter(MineScribePackType::name),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(MineScribePackType::folder),
            Codec.INT.fieldOf("version").forGetter(MineScribePackType::version),
            Codec.STRING.optionalFieldOf("versionKey").forGetter(MineScribePackType::versionKey)
    ).apply(instance, MineScribePackType::new));

    @Override
    public String toString() {
        return "%s (./%s)".formatted(MineScribeStringHelper.toTitleCase(this.label().getText()), folder.toString());
    }

    @Override
    public FancyText getLabel() {
        return this.label;
    }
}
