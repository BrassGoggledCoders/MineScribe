package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;

import java.util.Collections;
import java.util.List;

public record FileFieldInfo(
        String label,
        String field,
        int sortOrder,
        boolean required,
        List<FieldValidation> validations
) implements Comparable<FileFieldInfo> {
    public static final Codec<FileFieldInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(FileFieldInfo::label),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(FileFieldInfo::field),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(FileFieldInfo::sortOrder),
            Codec.BOOL.optionalFieldOf(JsonFieldNames.REQUIRED, false).forGetter(FileFieldInfo::required),
            FieldValidation.LIST_CODEC.optionalFieldOf("validations", Collections.emptyList()).forGetter(FileFieldInfo::validations)
    ).apply(instance, FileFieldInfo::new));

    public FileFieldInfo(
            String label,
            String field,
            int sortOrder,
            boolean required
    ) {
        this(label, field, sortOrder, required, Collections.emptyList());
    }


    @Override
    public int compareTo(@NotNull FileFieldInfo o) {
        int comparedSortOrder = Integer.compare(this.sortOrder(), o.sortOrder());
        if (comparedSortOrder == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.label(), o.label());
        } else {
            return comparedSortOrder;
        }
    }
}
