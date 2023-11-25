package xyz.brassgoggledcoders.minescribe.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

public class MineScribeCodecs {

    public static final Codec<Component> COMPONENT = new JsonCodec<>(
            Component.Serializer::fromJson,
            Component.Serializer::toJsonTree
    );

    public static final Codec<String> LABEL_STRING = COMPONENT.xmap(
            Component::getString,
            Component::literal
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
            LABEL_STRING.fieldOf("label").forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            MS_PACK_TYPE.fieldOf("packType").forGetter(PackContentParentType::getPackType)
    ).apply(instance, (label, path, form, packType) -> new PackContentParentType(label, path, form.orElse(null), packType)));

    public static final Codec<PackContentChildType> PACK_CONTENT_CHILD_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            LABEL_STRING.fieldOf("label").forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            RESOURCE_ID.fieldOf("parentId").forGetter(PackContentChildType::getParentId)
    ).apply(instance, (label, path, form, packType) -> new PackContentChildType(label, path, form.orElse(null), packType)));

    public static final Codec<ObjectType> OBJECT_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            RESOURCE_ID.fieldOf("id").forGetter(ObjectType::id),
            FileForm.CODEC.fieldOf("form").forGetter(ObjectType::fileForm)
    ).apply(instance, ObjectType::new));

    public static final Codec<SerializerType> SERIALIZER_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            RESOURCE_ID.fieldOf("parentId").forGetter(SerializerType::parentId),
            RESOURCE_ID.fieldOf("id").forGetter(SerializerType::id),
            RESOURCE_ID.fieldOf("serializerId").forGetter(SerializerType::serializerId),
            LABEL_STRING.fieldOf("label").forGetter(SerializerType::label),
            FileForm.CODEC.fieldOf("form").forGetter(SerializerType::fileForm)
    ).apply(instance, SerializerType::new));
}
