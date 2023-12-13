package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public record SingleSelectionFileFieldDefinition(List<IFormList<?>> formList) implements IFileFieldDefinition {
    public static final Codec<SingleSelectionFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(IFormList.CODEC, IFormList.CODEC.listOf())
                    .fieldOf(JsonFieldNames.LIST)
                    .<List<IFormList<?>>>xmap(
                            either -> either.map(List::of, Function.identity()),
                            list -> {
                                if (list.size() == 1) {
                                    return Either.left(list.get(0));
                                } else {
                                    return Either.right(list);
                                }
                            }
                    )
                    .forGetter(SingleSelectionFileFieldDefinition::formList)
    ).apply(instance, SingleSelectionFileFieldDefinition::new));

    public SingleSelectionFileFieldDefinition(IFormList<?> formList) {
        this(Collections.singletonList(formList));
    }

    @Override
    @NotNull
    public Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
