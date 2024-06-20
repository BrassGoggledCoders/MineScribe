package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.api.util.ConvertingUtil;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;

import java.nio.file.Path;
import java.util.Optional;

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
}
