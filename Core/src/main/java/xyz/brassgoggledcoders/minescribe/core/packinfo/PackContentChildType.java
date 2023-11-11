package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

import java.nio.file.Path;

public class PackContentChildType extends PackContentType implements IFullName {
    public static final Codec<PackContentChildType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("id").forGetter(PackContentType::getId),
            Codec.STRING.fieldOf("label").forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            ResourceId.CODEC.fieldOf("parentId").forGetter(PackContentChildType::getParentId)
    ).apply(instance, (id, label, path, form, packType) -> new PackContentChildType(id, label, path, form.orElse(null), packType)));

    private final ResourceId parentId;
    public PackContentChildType(ResourceId id, String label, Path path, FileForm form, ResourceId parentId) {
        super(id, label, path, form);
        this.parentId = parentId;
    }

    public ResourceId getParentId() {
        return parentId;
    }

    @Override
    public ResourceId getFullName() {
        return new ResourceId(
                this.getId().namespace(),
                "types/child/" + this.getId().path()
        );
    }
}
