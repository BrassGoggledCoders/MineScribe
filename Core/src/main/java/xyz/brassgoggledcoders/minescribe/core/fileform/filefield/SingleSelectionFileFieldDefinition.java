package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

public record SingleSelectionFileFieldDefinition(IFormList formList) implements IFileFieldDefinition {
    public static final Codec<SingleSelectionFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFormList.CODEC.fieldOf(JsonFieldNames.LIST).forGetter(SingleSelectionFileFieldDefinition::formList)
    ).apply(instance, SingleSelectionFileFieldDefinition::new));

    @Override
    @NotNull
    public Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }
}
