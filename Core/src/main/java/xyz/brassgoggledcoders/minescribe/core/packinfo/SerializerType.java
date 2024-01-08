package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

public record SerializerType(
        ResourceId parentId,
        String serializerId,
        FancyText label,
        FileForm fileForm
) implements IFullName, ILabeledValue {
    public static final Codec<SerializerType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("parentId").forGetter(SerializerType::parentId),
            Codec.STRING.fieldOf("serializerId").forGetter(SerializerType::serializerId),
            FancyText.CODEC.fieldOf("label").forGetter(SerializerType::label),
            FileForm.CODEC.fieldOf("form").forGetter(SerializerType::fileForm)
    ).apply(instance, SerializerType::new));

    @Override
    public ResourceId getFullName() {
        ResourceId id = Registries.getSerializerTypes()
                .getKey(this);
        return new ResourceId(
                id.namespace(),
                "types/serializer/" + id.path()
        );
    }

    @Override
    public FancyText getLabel() {
        return this.label();
    }
}
