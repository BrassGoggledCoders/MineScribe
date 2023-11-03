package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

public record ReferencedObjectFileFieldDefinition(ResourceId objectId) implements IFileFieldDefinition {
    public static final Codec<ReferencedObjectFileFieldDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("objectId").forGetter(ReferencedObjectFileFieldDefinition::objectId)
    ).apply(instance, ReferencedObjectFileFieldDefinition::new));

    @Override
    public @NotNull Codec<? extends IFileFieldDefinition> getCodec() {
        return CODEC;
    }


}
