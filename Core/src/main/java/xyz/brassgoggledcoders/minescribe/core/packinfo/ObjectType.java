package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

public record ObjectType(
        FileForm fileForm
) implements IFullName {
    public static final Codec<ObjectType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FileForm.CODEC.fieldOf("form").forGetter(ObjectType::fileForm)
    ).apply(instance, ObjectType::new));

    @Override
    public ResourceId getFullName() {
        ResourceId id = Registries.getObjectTypes()
                .getKey(this);
        return new ResourceId(
                id.namespace(),
                "types/object/" + id.path()
        );
    }
}
