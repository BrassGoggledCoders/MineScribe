package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.BiFunction;
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

    static <T extends IFileField> Codec<T> createCodec(
            BiFunction<RecordCodecBuilder.Instance<T>, Products.P3<RecordCodecBuilder.Mu<T>, String, String, Integer>, ? extends App<RecordCodecBuilder.Mu<T>, T>> completeCodec
    ) {
        return RecordCodecBuilder.create(instance -> completeCodec.apply(instance, instance.group(
                Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
                Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
                Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder)
        )));
    }
}
