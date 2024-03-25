package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.nio.file.Path;
import java.util.Optional;

public class PackContentChildType extends PackContentType implements IFullName {
    public static final Codec<PackContentChildType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileForm.CODEC).forGetter(PackContentType::getForm),
            ResourceId.CODEC.optionalFieldOf(JsonFieldNames.PARENT_ID).forGetter(type -> Optional.empty()),
            RootInfo.CODEC.optionalFieldOf(JsonFieldNames.ROOT_INFO).forGetter(type -> Optional.of(type.getRootInfo()))
    ).apply(instance, (label, path, form, parentId, rootInfo) -> new PackContentChildType(
            label,
            path,
            form.orElse(null),
            parentId.map(id -> new RootInfo(RootType.CONTENT, Optional.of(id)))
                    .or(() -> rootInfo)
                    .orElseThrow()
    )));

    private final ResourceId parentId;

    public PackContentChildType(FancyText label, Path path, FileForm form, RootInfo rootInfo) {
        super(label, path, form, rootInfo);
        this.parentId = rootInfo.id()
                .orElseThrow();
    }

    public ResourceId getParentId() {
        return parentId;
    }
}
