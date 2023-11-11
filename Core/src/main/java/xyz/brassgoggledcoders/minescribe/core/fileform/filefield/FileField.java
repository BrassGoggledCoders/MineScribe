package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

import java.util.List;

public record FileField<T extends IFileFieldDefinition>(
        T definition,
        FileFieldInfo info
) implements Comparable<FileField<?>> {
    public static final Codec<FileField<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFileFieldDefinition.CODEC.fieldOf(JsonFieldNames.DEFINITION).forGetter(FileField::definition),
            FileFieldInfo.CODEC.fieldOf(JsonFieldNames.INFO).forGetter(FileField::info)
    ).apply(instance, FileField::new));

    public static final Codec<List<FileField<?>>> LIST_CODEC = CODEC.listOf();

    @Override
    public int compareTo(@NotNull FileField o) {
        return this.info().compareTo(o.info());
    }
}
