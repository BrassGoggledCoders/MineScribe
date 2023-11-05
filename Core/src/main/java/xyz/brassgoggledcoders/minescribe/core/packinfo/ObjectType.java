package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

public record ObjectType(
        ResourceId id,
        FileForm fileForm
) implements IFullName {
    public static final Codec<ObjectType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("id").forGetter(ObjectType::id),
            FileForm.CODEC.fieldOf("form").forGetter(ObjectType::fileForm)
    ).apply(instance, ObjectType::new));

    @Override
    public ResourceId getFullName() {
        return new ResourceId(
                this.id().namespace(),
                "types/object/" + this.id().path()
        );
    }
}
