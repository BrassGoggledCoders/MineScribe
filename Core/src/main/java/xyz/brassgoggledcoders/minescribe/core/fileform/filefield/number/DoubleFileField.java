package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

public class DoubleFileField extends NumberFileField<Double> {
    public static final Codec<DoubleFileField> CODEC = IFileField.createCodec((instance, start) ->
            start.and(Range.DOUBLE_CODEC.fieldOf("range").forGetter(DoubleFileField::getRange))
                    .apply(instance, DoubleFileField::new)
    );

    public DoubleFileField(String name, String field, int sortOrder, Range<Double> range) {
        super(name, field, sortOrder, range);
    }

    @Override
    public Double convertNumber(Number number) {
        return number.doubleValue();
    }

    @Override
    public @NotNull Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
