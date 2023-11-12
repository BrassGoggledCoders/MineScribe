package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Optional;

public record SerializerInfo(
        String fieldName,
        String label,
        Optional<FileForm> defaultForm,
        Optional<ResourceId> defaultType
) {
    public static final Codec<SerializerInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("field", "type").forGetter(SerializerInfo::fieldName),
            Codec.STRING.fieldOf("label").forGetter(SerializerInfo::label),
            LazyCodec.of(() -> FileForm.CODEC).optionalFieldOf("defaultForm").forGetter(SerializerInfo::defaultForm),
            ResourceId.CODEC.optionalFieldOf("defaultType").forGetter(SerializerInfo::defaultType)
    ).apply(instance, SerializerInfo::new));

    public static SerializerInfo of(String fieldName, String label) {
        return new SerializerInfo(fieldName, label, Optional.empty(), Optional.empty());
    }

    public static SerializerInfo of(String fieldName, String label, FileForm fileForm) {
        return new SerializerInfo(fieldName, label, Optional.of(fileForm), Optional.empty());
    }
}
