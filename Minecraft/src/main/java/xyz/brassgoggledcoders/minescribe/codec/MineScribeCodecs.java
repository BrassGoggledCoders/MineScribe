package xyz.brassgoggledcoders.minescribe.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import xyz.brassgoggledcoders.minescribe.api.util.ConvertingUtil;
import xyz.brassgoggledcoders.minescribe.core.codec.EnumCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ObjectType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

public class MineScribeCodecs {

    public static final Codec<Component> COMPONENT = new JsonCodec<>(
            Component.Serializer::fromJson,
            Component.Serializer::toJsonTree
    );

    public static final Codec<FancyText> LABEL_STRING = COMPONENT.xmap(
            ConvertingUtil::convert,
            ConvertingUtil::convert
    );

    public static final Codec<PackType> PACK_TYPE = new EnumCodec<>(PackType.class);

    public static final Codec<ResourceId> RESOURCE_ID = ResourceLocation.CODEC.xmap(
            rl -> new ResourceId(rl.getNamespace(), rl.getPath()),
            rId -> new ResourceLocation(rId.namespace(), rId.path())
    );

    public static final Codec<PackContentType> CONTENT_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            LABEL_STRING.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileForm.CODEC).forGetter(PackContentType::getForm),
            RootInfo.CODEC.fieldOf(JsonFieldNames.ROOT_INFO).forGetter(PackContentType::getRootInfo)
    ).apply(instance, (label, path, form, packType) -> new PackContentType(label, path, form.orElse(null), packType)));

    public static final Codec<ObjectType> OBJECT_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(ObjectType::getLabel),
            FileForm.CODEC.fieldOf(JsonFieldNames.FORM).forGetter(ObjectType::fileForm)
    ).apply(instance, ObjectType::new));

    public static final Codec<SerializerType> SERIALIZER_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            RESOURCE_ID.fieldOf(JsonFieldNames.PARENT_ID).forGetter(SerializerType::parentId),
            Codec.STRING.fieldOf(JsonFieldNames.SERIALIZER_ID).forGetter(SerializerType::serializerId),
            LABEL_STRING.fieldOf(JsonFieldNames.LABEL).forGetter(SerializerType::label),
            FileForm.CODEC.fieldOf(JsonFieldNames.FORM).forGetter(SerializerType::fileForm)
    ).apply(instance, SerializerType::new));
}
