package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record SerializerInfo(
        String fieldName,
        String label,
        List<FileField<?>> defaultFields,
        Optional<ResourceId> defaultType
) {
    public static final Codec<SerializerInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("field", "type").forGetter(SerializerInfo::fieldName),
            Codec.STRING.fieldOf("label").forGetter(SerializerInfo::label),
            FileField.LIST_CODEC.optionalFieldOf("defaultFields", Collections.emptyList()).forGetter(SerializerInfo::defaultFields),
            ResourceId.CODEC.optionalFieldOf("defaultType").forGetter(SerializerInfo::defaultType)
    ).apply(instance, SerializerInfo::new));

    public static SerializerInfo of(String fieldName, String label, FileField<?>... defaultFields) {
        return new SerializerInfo(fieldName, label, Arrays.asList(defaultFields), Optional.empty());
    }
}
