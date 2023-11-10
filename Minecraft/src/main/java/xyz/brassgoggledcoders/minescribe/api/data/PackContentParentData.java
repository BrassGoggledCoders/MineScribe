package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record PackContentParentData(
        ResourceLocation id,
        Component label,
        Path path,
        PackType packType,
        Optional<FileFormData> form
) {
    public static final Codec<PackContentParentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(PackContentParentData::id),
            MineScribeCodecs.COMPONENT.fieldOf("label").forGetter(PackContentParentData::label),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentParentData::path),
            MineScribeCodecs.PACK_TYPE.fieldOf("packType").forGetter(PackContentParentData::packType),
            ErroringOptionalFieldCodec.of("form", FileFormData.CODEC).forGetter(PackContentParentData::form)
    ).apply(instance, PackContentParentData::new));

    public static JsonCodecProvider<PackContentParentData> createProvider(
            DataGenerator dataGenerator,
            ExistingFileHelper existingFileHelper,
            String modid,
            Consumer<Consumer<PackContentParentData>> collectValues
    ) {
        Map<ResourceLocation, PackContentParentData> values = new HashMap<>();
        collectValues.accept(packContentParentData -> values.put(packContentParentData.id(), packContentParentData));
        return new JsonCodecProvider<>(
                dataGenerator,
                existingFileHelper,
                modid,
                JsonOps.INSTANCE,
                MineScribeAPI.PACK_TYPE,
                "types/parent",
                CODEC,
                values
        );
    }
}
