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
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.Optional;

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

    public static final Codec<MineScribePackType> MS_PACK_TYPE = PACK_TYPE.xmap(
            mcPackType -> Registries.getPackTypeRegistry().getValue(mcPackType.name()),
            msPackType -> PackType.valueOf(msPackType.name())
    );

    public static final Codec<ResourceId> RESOURCE_ID = ResourceLocation.CODEC.xmap(
            rl -> new ResourceId(rl.getNamespace(), rl.getPath()),
            rId -> new ResourceLocation(rId.namespace(), rId.path())
    );

    public static final Codec<PackContentParentType> PACK_CONTENT_PARENT_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            LABEL_STRING.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileForm.CODEC).forGetter(PackContentType::getForm),
            MS_PACK_TYPE.fieldOf(JsonFieldNames.PACK_TYPE).forGetter(PackContentParentType::getPackType),
            RootInfo.CODEC.optionalFieldOf(JsonFieldNames.ROOT_INFO, RootInfo.NAMESPACE).forGetter(PackContentParentType::getRootInfo)
    ).apply(instance, (label, path, form, packType, parent) -> new PackContentParentType(label, path, form.orElse(null), packType, parent)));

    public static final Codec<PackContentChildType> PACK_CONTENT_CHILD_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            LABEL_STRING.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileForm.CODEC).forGetter(PackContentType::getForm),
            RESOURCE_ID.fieldOf(JsonFieldNames.PARENT_ID).xmap(
                    id -> new RootInfo(RootType.CONTENT, Optional.of(id)),
                    info -> info.id().orElseThrow()
            ).forGetter(PackContentChildType::getRootInfo)
    ).apply(instance, (label, path, form, packType) -> new PackContentChildType(label, path, form.orElse(null), packType)));

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
