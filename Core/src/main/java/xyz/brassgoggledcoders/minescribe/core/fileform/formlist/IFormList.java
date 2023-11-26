package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Function;

public interface IFormList<T> {
    Codec<IFormList<?>> CODEC = LazyCodec.of(() -> Registries.getFormListCodecs()
            .getCodec()
            .dispatch(
                    IFormList::getCodec,
                    Function.identity()
            )
    );

    @NotNull
    default String getLabel(T value) {
        if (value instanceof ILabeledValue labeledValue) {
            return labeledValue.getLabel();
        } else {
            return value.toString();
        }
    }

    @NotNull
    default String getKey(T value) {
        return value.toString();
    }

    List<T> getValues() throws Exception;

    default List<FormListValue> getFormListValues() throws Exception {
        return this.getValues()
                .stream()
                .map(value -> new FormListValue(
                        this.getKey(value),
                        this.getLabel(value)
                ))
                .toList();
    }

    @NotNull
    Codec<? extends IFormList<?>> getCodec();
}
