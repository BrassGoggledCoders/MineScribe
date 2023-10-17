package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public class ListOfFileField extends FileField {
    public static final Codec<ListOfFileField> CODEC = LazyCodec.of(() -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder),
            Codec.INT.optionalFieldOf("minimum", 1).forGetter(ListOfFileField::getMinimum),
            Codec.INT.optionalFieldOf("maximum", Integer.MAX_VALUE).forGetter(ListOfFileField::getMaximum),
            IFileField.CODEC.fieldOf("valueField").forGetter(ListOfFileField::getValueField)
    ).apply(instance, ListOfFileField::new)));

    private final int minimum;
    private final int maximum;
    private final IFileField valueField;

    public ListOfFileField(String name, String field, int sortOrder, int minimum, int maximum, IFileField valueField) {
        super(name, field, sortOrder);
        this.minimum = minimum;
        this.maximum = maximum;
        this.valueField = valueField;
    }

    @Override
    public Codec<? extends IFileField> getCodec() {
        return CODEC;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public IFileField getValueField() {
        return valueField;
    }
}
