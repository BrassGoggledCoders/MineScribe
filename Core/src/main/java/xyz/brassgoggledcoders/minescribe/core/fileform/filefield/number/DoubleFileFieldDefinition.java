package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

public class DoubleFileFieldDefinition extends NumberFileFieldDefinition<Double> {
    public static final Codec<DoubleFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Range.DOUBLE_CODEC.fieldOf("range").forGetter(DoubleFileFieldDefinition::getRange)
    ).apply(instance, DoubleFileFieldDefinition::new));

    public DoubleFileFieldDefinition(Range<Double> range) {
        super(range);
    }

    @Override
    public Double convertNumber(Number number) {
        return number.doubleValue();
    }

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
