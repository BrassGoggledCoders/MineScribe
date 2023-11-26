package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;

import java.util.List;

public record ListSelectionFileFieldDefinition(List<IFormList<?>> listNames) implements IFileFieldDefinition {
    public static final Codec<ListSelectionFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFormList.CODEC.listOf().fieldOf(JsonFieldNames.LISTS).forGetter(ListSelectionFileFieldDefinition::listNames)
    ).apply(instance, ListSelectionFileFieldDefinition::new));

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
