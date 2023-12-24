package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.ArrayList;
import java.util.List;

public record FormList(
        ResourceId id,
        FancyText label,
        List<String> values
) implements ILabeledValue {
    public static final Codec<FormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("id").forGetter(FormList::id),
            FancyText.CODEC.fieldOf("label").forGetter(FormList::label),
            Codec.STRING.listOf().fieldOf("values").forGetter(FormList::values)
    ).apply(instance, (id, label, values) -> {
        List<String> sorted = new ArrayList<>(values);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        return new FormList(id, label, sorted);
    }));

    @Override
    public FancyText getLabel() {
        return this.label();
    }
}
