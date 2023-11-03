package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public class CheckBoxFileFieldDefinition implements IFileFieldDefinition {
    public static final Codec<CheckBoxFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf(JsonFieldNames.DEFAULT_VALUE, false).forGetter(CheckBoxFileFieldDefinition::getDefaultValue)
    ).apply(instance, CheckBoxFileFieldDefinition::new));

    private final boolean defaultValue;

    public CheckBoxFileFieldDefinition(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
