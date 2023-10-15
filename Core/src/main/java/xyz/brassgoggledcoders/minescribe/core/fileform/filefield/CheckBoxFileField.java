package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;

public class CheckBoxFileField extends FileField {
    public static final Codec<CheckBoxFileField> CODEC = basicCodec(CheckBoxFileField::new);

    public CheckBoxFileField(String name, String field, int sortOrder) {
        super(name, field, sortOrder);
    }

    @Override
    public Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
