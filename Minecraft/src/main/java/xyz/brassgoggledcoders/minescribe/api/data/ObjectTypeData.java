package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

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

    public static JsonCodecProvider<ObjectTypeData> createProvider(
            DataGenerator dataGenerator,
            ExistingFileHelper existingFileHelper,
            String modid,
            Consumer<Consumer<ObjectTypeData>> collectValues
    ) {
        Map<ResourceLocation, ObjectTypeData> values = new HashMap<>();
        collectValues.accept(objectTypeData -> values.put(objectTypeData.id(), objectTypeData));
        return new JsonCodecProvider<>(
                dataGenerator,
                existingFileHelper,
                modid,
                JsonOps.INSTANCE,
                MineScribeAPI.PACK_TYPE,
                "types/object",
                CODEC,
                values
        );
    }
}
