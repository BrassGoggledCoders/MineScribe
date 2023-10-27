package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.List;
import java.util.Map;

public class ObjectFileField extends FileField {
    public static final Codec<ObjectFileField> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.LABEL).forGetter(IFileField::getLabel),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(IFileField::getField),
            Codec.INT.optionalFieldOf(JsonFieldNames.SORT_ORDER, 0).forGetter(IFileField::getSortOrder),
            Codec.BOOL.optionalFieldOf("includeType", false).forGetter(ObjectFileField::isIncludeType),
            ResourceId.CODEC.fieldOf("defaultType").forGetter(ObjectFileField::getDefaultType),
            Codec.unboundedMap(ResourceId.CODEC, IFileField.LIST_CODEC).fieldOf("fieldsByType").forGetter(ObjectFileField::fieldsByType)
    ).apply(instance, ObjectFileField::new));
    private final Map<ResourceId, List<IFileField>> typedFields;
    private final ResourceId defaultType;
    private final boolean includeType;

    public ObjectFileField(String label, String field, int sortOrder, boolean includeType, ResourceId defaultType,
                           Map<ResourceId, List<IFileField>> typedFields) {
        super(label, field, sortOrder);
        this.includeType = includeType;
        this.typedFields = typedFields;
        this.defaultType = defaultType;
    }

    public boolean isIncludeType() {
        return includeType;
    }

    public Map<ResourceId, List<IFileField>> fieldsByType() {
        return typedFields;
    }

    @Override
    public @NotNull Codec<? extends IFileField> getCodec() {
        return CODEC;
    }

    public ResourceId getDefaultType() {
        return defaultType;
    }
}
