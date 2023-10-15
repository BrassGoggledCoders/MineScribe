package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

import java.util.List;

public class ListSelectionFileField extends FileField {
    public static final Codec<ListSelectionFileField> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder),
            Codec.STRING.listOf().fieldOf(JsonFieldNames.LISTS).forGetter(ListSelectionFileField::getListNames)
    ).apply(instance, ListSelectionFileField::new));
    private final List<String> listNames;

    public ListSelectionFileField(String name, String field, int sortOrder, List<String> listNames) {
        super(name, field, sortOrder);
        this.listNames = listNames;
    }

    public List<String> getListNames() {
        return this.listNames;
    }

    @Override
    public Codec<? extends IFileField> getCodec() {
        return CODEC;
    }
}
