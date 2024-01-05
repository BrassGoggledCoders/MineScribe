package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

public record ObjectType(
        FancyText label,
        FileForm fileForm
) implements IFullName, ILabeledValue {
    public static final Codec<ObjectType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(ObjectType::label),
            FileForm.CODEC.fieldOf("form").forGetter(ObjectType::fileForm)
    ).apply(instance, ObjectType::new));

    @Override
    public FancyText getLabel() {
        return this.label;
    }

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
