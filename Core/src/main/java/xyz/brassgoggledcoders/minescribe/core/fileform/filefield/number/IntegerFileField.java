package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

public class IntegerFileField extends NumberFileField<Integer> {
    public static final Codec<IntegerFileField> CODEC = IFileField.createCodec((instance, start) ->
            start.and(Range.INT_CODEC.fieldOf("range").forGetter(IntegerFileField::getRange))
                    .apply(instance, IntegerFileField::new)
    );

    public IntegerFileField(String name, String field, int sortOrder, Range<Integer> range) {
        super(name, field, sortOrder, range);
    }

    @Override
    public Integer convertNumber(Number number) {
        return number.intValue();
    }

    @Override
    public @NotNull Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
