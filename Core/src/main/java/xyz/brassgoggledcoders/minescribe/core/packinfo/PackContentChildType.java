package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.nio.file.Path;

public class PackContentChildType extends PackContentType implements IFullName {
    public static final Codec<PackContentChildType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            ResourceId.CODEC.fieldOf("parentId").forGetter(PackContentChildType::getParentId)
    ).apply(instance, (label, path, form, packType) -> new PackContentChildType(label, path, form.orElse(null), packType)));

    private final ResourceId parentId;

    public PackContentChildType(String label, Path path, FileForm form, ResourceId parentId) {
        super(label, path, form);
        this.parentId = parentId;
    }

    public ResourceId getParentId() {
        return parentId;
    }

    @Override
    public ResourceId getFullName() {
        ResourceId id = Registries.getContentChildTypes()
                .getKey(this);
        return new ResourceId(
                id.namespace(),
                "types/child/" + id.path()
        );
    }
}
