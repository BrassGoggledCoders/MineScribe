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
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record PackContentData(
        ResourceLocation id,
        RootInfoData rootInfo,
        Component label,
        Path path,
        Optional<FileFormData> form
) {
    public static final Codec<PackContentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf(JsonFieldNames.ID).forGetter(PackContentData::id),
            RootInfoData.CODEC.fieldOf(JsonFieldNames.ROOT_INFO).forGetter(PackContentData::rootInfo),
            MineScribeCodecs.COMPONENT.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentData::label),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentData::path),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileFormData.CODEC).forGetter(PackContentData::form)
    ).apply(instance, PackContentData::new));

    public PackContentType toType() {
        return new PackContentType(
                ConvertingUtil.convert(this.label()),
                this.path(),
                this.form()
                        .map(FileFormData::toFileForm)
                        .orElse(null),
                this.rootInfo()
                        .toInfo()
        );
    }


    public static JsonCodecProvider<PackContentData> createProvider(
            DataGenerator dataGenerator,
            ExistingFileHelper existingFileHelper,
            String modid,
            Consumer<Consumer<PackContentData>> collectValues
    ) {
        Map<ResourceLocation, PackContentData> values = new HashMap<>();
        collectValues.accept(contentData -> {
            if (contentData.rootInfo().type() == RootType.CONTENT) {
                ResourceLocation parentId = contentData.rootInfo.id()
                    .orElseThrow(() -> new IllegalStateException("No Content Parent set"));
                ResourceLocation actualParentId = new ResourceLocation(
                        parentId.getNamespace(),
                        "types/content/" + parentId.getPath() + ".json"
                );

                if (existingFileHelper.exists(actualParentId, MineScribeAPI.PACK_TYPE)) {
                    values.put(contentData.id(), contentData);
                } else {
                    throw new IllegalStateException("No Content Parent with Id %s".formatted(parentId));
                }
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
