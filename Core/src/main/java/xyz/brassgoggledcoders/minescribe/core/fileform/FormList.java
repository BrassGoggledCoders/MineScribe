package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.ArrayList;
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
    ).apply(instance, (id, label, values) -> {
        List<String> sorted = new ArrayList<>(values);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        return new FormList(id, label, sorted);
    }));
}
