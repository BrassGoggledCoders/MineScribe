package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public record SerializerTypeData(
        ResourceLocation id,
        ResourceLocation serializerId,
        ResourceLocation parentId,
        FileForm form
) {
    public static final Codec<SerializerTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SerializerTypeData::id),
            ResourceLocation.CODEC.fieldOf("serializerId").forGetter(SerializerTypeData::serializerId),
            ResourceLocation.CODEC.fieldOf("parentId").forGetter(SerializerTypeData::parentId),
            FileForm.CODEC.fieldOf("form").forGetter(SerializerTypeData::form)
    ).apply(instance, SerializerTypeData::new));

    public static JsonCodecProvider<SerializerTypeData> createProvider(
            DataGenerator dataGenerator,
            ExistingFileHelper existingFileHelper,
            String modid,
            Consumer<Consumer<SerializerTypeData>> collectValues
    ) {
        Map<ResourceLocation, SerializerTypeData> values = new HashMap<>();
        collectValues.accept(serializerTypeData -> {
            ResourceLocation properParentId = new ResourceLocation(
                    serializerTypeData.parentId().getNamespace(),
                    serializerTypeData.parentId().getPath() + ".json"
            );
            if (existingFileHelper.exists(properParentId, MineScribeAPI.PACK_TYPE)) {
                values.put(serializerTypeData.id(), serializerTypeData);
            } else {
                throw new IllegalStateException("No Parent with Id %s".formatted(serializerTypeData.parentId()));
            }
        });
        return new JsonCodecProvider<>(
                dataGenerator,
                existingFileHelper,
                modid,
                JsonOps.INSTANCE,
                MineScribeAPI.PACK_TYPE,
                "types/serializer",
                CODEC,
                values
        );
    }
}
