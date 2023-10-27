package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

public class SingleSelectionFileField extends FileField {
    public static final Codec<SingleSelectionFileField> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder),
            ResourceId.CODEC.fieldOf(JsonFieldNames.LIST).forGetter(SingleSelectionFileField::getListId)
    ).apply(instance, SingleSelectionFileField::new));

    private final ResourceId listId;

    public SingleSelectionFileField(String label, String field, int sortOrder, ResourceId listId) {
        super(label, field, sortOrder);
        this.listId = listId;
    }

    public ResourceId getListId() {
        return this.listId;
    }

    @Override
    @NotNull
    public Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
