package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

public abstract class FileField implements IFileField {
    private final String label;
    private final String field;
    private final int sortOrder;

    public FileField(String name, String field, int sortOrder) {
        this.label = name;
        this.field = field;
        this.sortOrder = sortOrder;
    }

    public String getLabel() {
        return label;
    }

    public String getField() {
        return field;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public int compareTo(@NotNull IFileField o) {
        int comparedSortOrder = Integer.compare(this.sortOrder, o.getSortOrder());
        if (comparedSortOrder == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.label, o.getLabel());
        } else {
            return 0;
        }
    }

    public static <T extends IFileField> Codec<T> basicCodec(Function3<String, String, Integer, T> create) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
                Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
                Codec.INT.fieldOf(JsonFieldNames.SORT_ORDER).forGetter(IFileField::getSortOrder)
        ).apply(instance, create));
    }
}
