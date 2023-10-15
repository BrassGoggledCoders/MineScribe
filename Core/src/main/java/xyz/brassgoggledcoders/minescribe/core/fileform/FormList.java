package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.List;

public record FormList(
        ResourceId id,
        String label,
        List<String> values
) {
    public static final Codec<FormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("id").forGetter(FormList::id),
            Codec.STRING.fieldOf("label").forGetter(FormList::label),
            Codec.STRING.listOf().fieldOf("values").forGetter(FormList::values)
    ).apply(instance, FormList::new));
}
