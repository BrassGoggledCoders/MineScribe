package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record FileFieldInfoData(
        String label,
        String field,
        int sortOrder,
        boolean required,
        List<ValidationData<?>> validations,
        Optional<String> helpText
) implements Comparable<FileFieldInfoData> {
    public static final Codec<FileFieldInfoData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(FileFieldInfoData::label),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(FileFieldInfoData::field),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(FileFieldInfoData::sortOrder),
            Codec.BOOL.optionalFieldOf(JsonFieldNames.REQUIRED, false).forGetter(FileFieldInfoData::required),
            ValidationData.LIST_CODEC.optionalFieldOf(JsonFieldNames.VALIDATIONS, Collections.emptyList())
                    .forGetter(FileFieldInfoData::validations),
            Codec.STRING.optionalFieldOf(JsonFieldNames.HELP_TEXT).forGetter(FileFieldInfoData::helpText)
    ).apply(instance, FileFieldInfoData::new));

    public FileFieldInfoData(
            String label,
            String field,
            int sortOrder,
            boolean required
    ) {
        this(label, field, sortOrder, required, Collections.emptyList());
    }

    public FileFieldInfoData(
            String label,
            String field,
            int sortOrder,
            boolean required,
            List<ValidationData<?>> validations
    ) {
        this(label, field, sortOrder, required, validations, Optional.empty());
    }


    @Override
    public int compareTo(@NotNull FileFieldInfoData o) {
        int comparedSortOrder = Integer.compare(this.sortOrder(), o.sortOrder());
        if (comparedSortOrder == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.label(), o.label());
        } else {
            return comparedSortOrder;
        }
    }

    public FileFieldInfo toFileInfo() {
        return new FileFieldInfo(
                this.label(),
                this.field(),
                this.sortOrder(),
                this.required(),
                new ArrayList<>(this.validations()),
                this.helpText()
        );
    }
}
