package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public record CheckBoxFileFieldDefinition(boolean defaultValue) implements IFileFieldDefinition {
    public static final Codec<CheckBoxFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf(JsonFieldNames.DEFAULT_VALUE, false).forGetter(CheckBoxFileFieldDefinition::defaultValue)
    ).apply(instance, CheckBoxFileFieldDefinition::new));

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
