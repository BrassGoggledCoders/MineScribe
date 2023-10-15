package xyz.brassgoggledcoders.minescribe.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

public class MineScribeCodecs {

    public static final Codec<Component> COMPONENT = new JsonCodec<>(
            Component.Serializer::fromJson,
            Component.Serializer::toJsonTree
    );

    public static final Codec<PackType> PACK_TYPE = new EnumCodec<>(PackType.class);

    public static final Codec<PackContentParentType> PACK_CONTENT_PARENT_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").xmap(
                    rl -> new ResourceId(rl.getNamespace(), rl.getPath()),
                    rId -> new ResourceLocation(rId.namespace(), rId.path())
            ).forGetter(PackContentType::getId),
            COMPONENT.fieldOf("label").xmap(
                    Component::getString,
                    Component::literal
            ).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            PACK_TYPE.fieldOf("packType").xmap(
                    mcPackType -> Registries.getPackTypes().getValue(mcPackType.name()),
                    msPackType -> PackType.valueOf(msPackType.name())
            ).forGetter(PackContentParentType::getPackType)
    ).apply(instance, (id, label, path, form, packType) -> new PackContentParentType(id, label, path, form.orElse(null), packType)));

    public static final Codec<PackContentChildType> PACK_CONTENT_CHILD_TYPE = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").xmap(
                    rl -> new ResourceId(rl.getNamespace(), rl.getPath()),
                    rId -> new ResourceLocation(rId.namespace(), rId.path())
            ).forGetter(PackContentType::getId),
            COMPONENT.fieldOf("label").xmap(
                    Component::getString,
                    Component::literal
            ).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            ResourceLocation.CODEC.fieldOf("parentId").xmap(
                    rl -> new ResourceId(rl.getNamespace(), rl.getPath()),
                    rId -> new ResourceLocation(rId.namespace(), rId.path())
            ).forGetter(PackContentType::getId)
    ).apply(instance, (id, label, path, form, packType) -> new PackContentChildType(id, label, path, form.orElse(null), packType)));
}
