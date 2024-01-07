package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.List;
import java.util.Optional;
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
    default FancyText getLabel(T value) {
        if (value instanceof ILabeledValue labeledValue) {
            return labeledValue.getLabel();
        } else {
            return FancyText.literal(value.toString());
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
                        this.getLabel(value),
                        this.getAlias(value)
                ))
                .toList();
    }

    /**
     * Actually sorta id? Because Registries with Aliases are actually considered their ids
     * Merely a second name to check, but id should be the written one.
     */
    default Optional<String> getAlias(T value) {
        return Optional.empty();
    }

    @NotNull
    Codec<? extends IFormList<?>> getCodec();
}
