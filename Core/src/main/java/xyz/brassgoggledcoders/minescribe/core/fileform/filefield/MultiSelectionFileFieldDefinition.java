package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;

import java.util.List;

public record MultiSelectionFileFieldDefinition(List<IFormList<?>> formLists) implements IFileFieldDefinition {
    public static final Codec<MultiSelectionFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFormList.CODEC.listOf().fieldOf(JsonFieldNames.LISTS).forGetter(MultiSelectionFileFieldDefinition::formLists)
    ).apply(instance, MultiSelectionFileFieldDefinition::new));

    @Override
    @NotNull
    public Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
