package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

public class IntegerFileFieldDefinition extends NumberFileFieldDefinition<Integer> {
    public static final Codec<IntegerFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Range.INT_CODEC.fieldOf(JsonFieldNames.RANGE).forGetter(IntegerFileFieldDefinition::getRange)
    ).apply(instance, IntegerFileFieldDefinition::new));

    public IntegerFileFieldDefinition(Range<Integer> range) {
        super(range);
    }

    @Override
    public Integer convertNumber(Number number) {
        return number.intValue();
    }

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
