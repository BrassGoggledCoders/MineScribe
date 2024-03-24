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
import xyz.brassgoggledcoders.minescribe.api.util.ConvertingUtil;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record PackContentChildData(
        ResourceLocation id,
        ResourceLocation parentId,
        Component label,
        Path path,
        Optional<FileFormData> form
) {
    public static final Codec<PackContentChildData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf(JsonFieldNames.ID).forGetter(PackContentChildData::id),
            ResourceLocation.CODEC.fieldOf(JsonFieldNames.PARENT_ID).forGetter(PackContentChildData::parentId),
            MineScribeCodecs.COMPONENT.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentChildData::label),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentChildData::path),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileFormData.CODEC).forGetter(PackContentChildData::form)
    ).apply(instance, PackContentChildData::new));

    public PackContentChildType toType() {
        return new PackContentChildType(
                ConvertingUtil.convert(this.label()),
                this.path(),
                this.form()
                        .map(FileFormData::toFileForm)
                        .orElse(null),
                new RootInfo(
                        RootType.CONTENT,
                        Optional.of(new ResourceId(this.parentId().getNamespace(), this.parentId().getPath()))
                )
        );
    }


    public static JsonCodecProvider<PackContentChildData> createProvider(
            DataGenerator dataGenerator,
            ExistingFileHelper existingFileHelper,
            String modid,
            Consumer<Consumer<PackContentChildData>> collectValues
    ) {
        Map<ResourceLocation, PackContentChildData> values = new HashMap<>();
        collectValues.accept(packContentChildData -> {
            ResourceLocation actualParentId = new ResourceLocation(
                    packContentChildData.parentId().getNamespace(),
                    "types/parent/" + packContentChildData.parentId().getPath() + ".json"
            );
            if (existingFileHelper.exists(actualParentId, MineScribeAPI.PACK_TYPE)) {
                values.put(packContentChildData.id(), packContentChildData);
            } else {
                throw new IllegalStateException("No Pack Content Parent with Id %s".formatted(packContentChildData.parentId()));
            }
        });
        return new JsonCodecProvider<>(
                dataGenerator,
                existingFileHelper,
                modid,
                JsonOps.INSTANCE,
                MineScribeAPI.PACK_TYPE,
                "types/child",
                CODEC,
                values
        );
    }
}
