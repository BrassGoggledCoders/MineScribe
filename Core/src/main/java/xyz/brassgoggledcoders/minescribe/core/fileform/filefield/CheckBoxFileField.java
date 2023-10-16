package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public class CheckBoxFileField extends FileField {
    public static final Codec<CheckBoxFileField> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder),
            Codec.BOOL.optionalFieldOf(JsonFieldNames.DEFAULT_VALUE, false).forGetter(CheckBoxFileField::getDefaultValue)
    ).apply(instance, CheckBoxFileField::new));

    private final boolean defaultValue;

    public CheckBoxFileField(String name, String field, int sortOrder, boolean defaultValue) {
        super(name, field, sortOrder);
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
