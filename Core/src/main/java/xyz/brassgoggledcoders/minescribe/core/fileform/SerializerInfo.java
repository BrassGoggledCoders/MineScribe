package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record SerializerInfo(
        String fieldName,
        String label,
        List<IFileField> defaultFields,
        Optional<ResourceId> defaultType
) {
    public static final Codec<SerializerInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("field", "type").forGetter(SerializerInfo::fieldName),
            Codec.STRING.fieldOf("label").forGetter(SerializerInfo::label),
            IFileField.LIST_CODEC.optionalFieldOf("defaultFields", Collections.emptyList()).forGetter(SerializerInfo::defaultFields),
            ResourceId.CODEC.optionalFieldOf("defaultType").forGetter(SerializerInfo::defaultType)
    ).apply(instance, SerializerInfo::new));

    public static SerializerInfo of(String fieldName, String label, IFileField... defaultFields) {
        return new SerializerInfo(fieldName, label, Arrays.asList(defaultFields), Optional.empty());
    }
}
