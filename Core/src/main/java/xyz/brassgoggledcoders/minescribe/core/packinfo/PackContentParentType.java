package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

import java.nio.file.Path;

public class PackContentParentType extends PackContentType {
    public static final Codec<PackContentParentType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf("id").forGetter(PackContentType::getId),
            Codec.STRING.fieldOf("label").forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            FileForm.CODEC.optionalFieldOf("form").forGetter(PackContentType::getForm),
            //TODO MAKE DISPATCH
            MineScribePackType.CODEC.fieldOf("packType").forGetter(PackContentParentType::getPackType)
    ).apply(instance, (id, label, path, form, packType) -> new PackContentParentType(id, label, path, form.orElse(null), packType)));

    private final MineScribePackType packType;
    public PackContentParentType(ResourceId id, String label, Path path, FileForm form, MineScribePackType packType) {
        super(id, label, path, form);
        this.packType = packType;
    }

    public MineScribePackType getPackType() {
        return packType;
    }
}
