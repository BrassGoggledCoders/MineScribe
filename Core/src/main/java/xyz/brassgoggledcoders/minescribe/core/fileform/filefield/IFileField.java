package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;

public interface IFileField extends Comparable<IFileField> {
    String getLabel();

    String getField();

    int getSortOrder();

    Codec<? extends IFileField> getCodec();
}
