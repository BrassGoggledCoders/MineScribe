package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;

import java.util.List;

public record FileFieldData<T extends IFileFieldDefinition>(
        T definition,
        FileFieldInfoData info
) implements Comparable<FileFieldData<?>> {
    public static final Codec<FileFieldData<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFileFieldDefinition.CODEC.fieldOf(JsonFieldNames.DEFINITION).forGetter(FileFieldData::definition),
            FileFieldInfoData.CODEC.fieldOf(JsonFieldNames.INFO).forGetter(FileFieldData::info)
    ).apply(instance, FileFieldData::new));

    public static final Codec<List<FileFieldData<?>>> LIST_CODEC = CODEC.listOf();

    public FileField<T> toFileField() {
        return new FileField<>(
                this.definition(),
                this.info().toFileInfo()
        );
    }

    @Override
    public int compareTo(@NotNull FileFieldData o) {
        return this.info().compareTo(o.info());
    }
}
