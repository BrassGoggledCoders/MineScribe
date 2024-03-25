package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ErroringOptionalFieldCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.MineScribeCoreCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.nio.file.Path;
import java.util.Optional;

public class PackContentType implements ILabeledValue, IFullName {
    public static final Codec<PackContentType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FancyText.CODEC.fieldOf(JsonFieldNames.LABEL).forGetter(PackContentType::getLabel),
            MineScribeCoreCodecs.PATH.fieldOf(JsonFieldNames.PATH).forGetter(PackContentType::getPath),
            ErroringOptionalFieldCodec.of(JsonFieldNames.FORM, FileForm.CODEC).forGetter(PackContentType::getForm),
            RootInfo.CODEC.optionalFieldOf(JsonFieldNames.ROOT_INFO, RootInfo.NAMESPACE).forGetter(PackContentType::getRootInfo)
    ).apply(instance, (label, path, form, rootInfo) -> new PackContentType(
            label,
            path,
            form.orElse(null),
            rootInfo
    )));

    private final FancyText label;
    private final Path path;
    private final FileForm form;
    private final RootInfo rootInfo;

    public PackContentType(FancyText label, Path path, FileForm form, RootInfo rootInfo) {
        this.label = label;
        this.path = path;
        this.form = form;
        this.rootInfo = rootInfo;
    }

    @Override
    public FancyText getLabel() {
        return label;
    }

    public Path getPath() {
        return path;
    }

    public Optional<FileForm> getForm() {
        return Optional.ofNullable(form);
    }

    public RootInfo getRootInfo() {
        return rootInfo;
    }

    @Override
    public ResourceId getFullName() {
        ResourceId id = Registries.getContentTypes()
                .getKey(this);
        return new ResourceId(
                id.namespace(),
                "types/content/" + id.path()
        );
    }
}
