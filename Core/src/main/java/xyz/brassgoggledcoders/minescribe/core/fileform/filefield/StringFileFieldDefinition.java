package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public record StringFileFieldDefinition(String defaultValue) implements IFileFieldDefinition {
    public static final Codec<StringFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf(JsonFieldNames.DEFAULT_VALUE, "").forGetter(StringFileFieldDefinition::defaultValue)
    ).apply(instance, StringFileFieldDefinition::new));

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
