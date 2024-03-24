package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.nio.file.Path;

public class PackContentParentType extends PackContentType implements IFullName {
    public static final Codec<PackContentParentType> CODEC = LazyCodec.of(() -> RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileForm.CODEC).forGetter(PackContentType::getForm),
            LazyCodec.of(() -> Registries.getPackTypeRegistry().getCodec())
                    .fieldOf(JsonFieldNames.PACK_TYPE).forGetter(PackContentParentType::getPackType),
            RootInfo.CODEC.optionalFieldOf(JsonFieldNames.ROOT_INFO, RootInfo.NAMESPACE).forGetter(PackContentParentType::getRootInfo)
    ).apply(instance, (label, path, form, packType, parent) -> new PackContentParentType(label, path, form.orElse(null), packType, parent))));

    private final MineScribePackType packType;

    public PackContentParentType(FancyText label, Path path, FileForm form, MineScribePackType packType, RootInfo rootInfo) {
        super(label, path, form, rootInfo);
        this.packType = packType;
    }

    public MineScribePackType getPackType() {
        return packType;
    }

    @Override
    public ResourceId getFullName() {
        ResourceId id = Registries.getContentParentTypes()
                .getKey(this);
        return new ResourceId(
                id.namespace(),
                "types/parent/" + id.path()
        );
    }


}
