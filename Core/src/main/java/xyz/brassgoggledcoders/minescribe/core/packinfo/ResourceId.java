package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResourceId(
        String namespace,
        String path
) {
    public static final Codec<ResourceId> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("namespace").forGetter(ResourceId::namespace),
            Codec.STRING.fieldOf("path").forGetter(ResourceId::path)
    ).apply(instance, ResourceId::new));

}
