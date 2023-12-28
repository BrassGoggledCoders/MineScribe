package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Optional;

public record ReferencedObjectFileFieldDefinition(
        ResourceId objectId,
        Optional<StringObjectTransform> transform
) implements IFileFieldDefinition {
    public static final Codec<ReferencedObjectFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("objectId").forGetter(ReferencedObjectFileFieldDefinition::objectId),
            StringObjectTransform.CODEC.optionalFieldOf("transform").forGetter(ReferencedObjectFileFieldDefinition::transform)
    ).apply(instance, ReferencedObjectFileFieldDefinition::new));

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }


}
