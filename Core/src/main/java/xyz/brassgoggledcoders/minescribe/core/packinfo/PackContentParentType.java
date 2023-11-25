package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.nio.file.Path;

public class PackContentParentType extends PackContentType implements IFullName {
    public static final Codec<PackContentParentType> CODEC = LazyCodec.of(() -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf("path").forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of("form", FileForm.CODEC).forGetter(PackContentType::getForm),
            LazyCodec.of(() -> Registries.getPackTypeRegistry().getCodec())
                    .fieldOf("packType").forGetter(PackContentParentType::getPackType)
    ).apply(instance, (label, path, form, packType) -> new PackContentParentType(label, path, form.orElse(null), packType))));

    private final MineScribePackType packType;

    public PackContentParentType(String label, Path path, FileForm form, MineScribePackType packType) {
        super(label, path, form);
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
