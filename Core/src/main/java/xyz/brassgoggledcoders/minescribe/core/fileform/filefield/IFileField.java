package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Function;

public interface IFileField extends Comparable<IFileField> {
    Codec<IFileField> CODEC = LazyCodec.of(() -> Registries.getFileFieldCodecRegistry()
            .getCodec()
            .dispatch(
                    IFileField::getCodec,
                    Function.identity()
            )
    );

    Codec<List<IFileField>> LIST_CODEC = LazyCodec.of(CODEC::listOf);

    String getLabel();

    String getField();

    int getSortOrder();

    @NotNull
    Codec<? extends IFileField> getCodec();

    @Override
    default int compareTo(@NotNull IFileField o) {
        int comparedSortOrder = Integer.compare(this.getSortOrder(), o.getSortOrder());
        if (comparedSortOrder == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.getLabel(), o.getLabel());
        } else {
            return comparedSortOrder;
        }
    }
}
