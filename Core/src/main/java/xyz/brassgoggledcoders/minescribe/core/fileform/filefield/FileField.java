package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FileField(
        IFileFieldDefinition definition,
        FileFieldInfo info
) implements Comparable<FileField> {
    public static final Codec<FileField> CODEC = Codec.pair(
            IFileFieldDefinition.CODEC,
            FileFieldInfo.CODEC
    ).xmap(
            pair -> new FileField(pair.getFirst(), pair.getSecond()),
            fileField -> Pair.of(fileField.definition(), fileField.info())
    );

    public static final Codec<List<FileField>> LIST_CODEC = CODEC.listOf();

    @Override
    public int compareTo(@NotNull FileField o) {
        return this.info().compareTo(o.info());
    }
}
