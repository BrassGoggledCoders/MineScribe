package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Optional;

public record StringObjectTransform(
        Optional<ResourceId> type,
        String field
) {
    public static final Codec<StringObjectTransform> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceId.CODEC.optionalFieldOf(JsonFieldNames.TYPE).forGetter(StringObjectTransform::type),
            Codec.STRING.fieldOf(JsonFieldNames.FIELD).forGetter(StringObjectTransform::field)
    ).apply(inst, StringObjectTransform::new));
}
