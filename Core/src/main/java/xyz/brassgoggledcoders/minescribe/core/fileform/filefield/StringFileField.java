package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public class StringFileField extends FileField {
    public static final Codec<StringFileField> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder),
            Codec.STRING.optionalFieldOf(JsonFieldNames.DEFAULT_VALUE, "").forGetter(StringFileField::getDefaultValue)
    ).apply(instance, StringFileField::new));

    private final String defaultValue;
    public StringFileField(String name, String field, int sortOrder, String defaultValue) {
        super(name, field, sortOrder);
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
