package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.SetCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface IFileField extends Comparable<IFileField> {
    Codec<IFileField> CODEC = LazyCodec.of(() -> Registries.getFileFieldCodecRegistry()
            .getCodec()
            .dispatch(
                    IFileField::getCodec,
                    Function.identity()
            )
    );

    Codec<Set<IFileField>> SET_CODEC = LazyCodec.of(() -> new SetCodec<>(CODEC));

    String getLabel();

    String getField();

    int getSortOrder();

    Codec<? extends IFileField> getCodec();

    @Override
    default int compareTo(@NotNull IFileField o) {
        int comparedSortOrder = Integer.compare(this.getSortOrder(), o.getSortOrder());
        if (comparedSortOrder == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.getField(), o.getLabel());
        } else {
            return 0;
        }
    }
}
