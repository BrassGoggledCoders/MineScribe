package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;

public class ListOfFileFieldDefinition implements IFileFieldDefinition {
    public static final Codec<ListOfFileFieldDefinition> CODEC = LazyCodec.of(() -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("minimum", 1).forGetter(ListOfFileFieldDefinition::getMinimum),
            Codec.INT.optionalFieldOf("maximum", Integer.MAX_VALUE).forGetter(ListOfFileFieldDefinition::getMaximum),
            IFileFieldDefinition.CODEC.fieldOf("valueField").forGetter(ListOfFileFieldDefinition::getChildField)
    ).apply(instance, ListOfFileFieldDefinition::new)));

    private final int minimum;
    private final int maximum;
    private final IFileFieldDefinition valueField;

    public ListOfFileFieldDefinition(int minimum, int maximum, IFileFieldDefinition valueField) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.valueField = valueField;
    }

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public IFileFieldDefinition getChildField() {
        return valueField;
    }
}
