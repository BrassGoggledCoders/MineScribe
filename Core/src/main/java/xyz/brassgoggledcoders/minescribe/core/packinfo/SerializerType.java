package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

public record SerializerType(
        ResourceId parentId,
        ResourceId id,
        ResourceId serializerId,
        String label,
        FileForm fileForm
) implements IFullName {
    public static final Codec<SerializerType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("parentId").forGetter(SerializerType::parentId),
            ResourceId.CODEC.fieldOf("id").forGetter(SerializerType::id),
            ResourceId.CODEC.fieldOf("serializerId").forGetter(SerializerType::serializerId),
            Codec.STRING.fieldOf("label").forGetter(SerializerType::label),
            FileForm.CODEC.fieldOf("form").forGetter(SerializerType::fileForm)
    ).apply(instance, SerializerType::new));

    @Override
    public ResourceId getFullName() {
        return new ResourceId(
                this.id().namespace(),
                "types/serializer/" + this.id().path()
        );
    }
}
