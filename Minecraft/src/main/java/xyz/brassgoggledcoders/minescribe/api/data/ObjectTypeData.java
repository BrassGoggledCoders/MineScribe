package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public record ObjectTypeData(
        ResourceLocation id,
        FileFormData form
) {
    public static final Codec<ObjectTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ObjectTypeData::id),
            FileFormData.CODEC.fieldOf("form").forGetter(ObjectTypeData::form)
    ).apply(instance, ObjectTypeData::new));
}
