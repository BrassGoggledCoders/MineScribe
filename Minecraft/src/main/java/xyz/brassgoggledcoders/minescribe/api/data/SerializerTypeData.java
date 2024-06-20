package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;

public record SerializerTypeData(
        ResourceLocation id,
        ResourceLocation serializerId,
        ResourceLocation parentId,
        Component label,
        FileFormData form
) {
    public static final Codec<SerializerTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SerializerTypeData::id),
            ResourceLocation.CODEC.fieldOf("serializerId").forGetter(SerializerTypeData::serializerId),
            ResourceLocation.CODEC.fieldOf("parentId").forGetter(SerializerTypeData::parentId),
            MineScribeCodecs.COMPONENT.fieldOf("label").forGetter(SerializerTypeData::label),
            FileFormData.CODEC.fieldOf("form").forGetter(SerializerTypeData::form)
    ).apply(instance, SerializerTypeData::new));
}
