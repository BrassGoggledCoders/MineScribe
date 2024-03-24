package xyz.brassgoggledcoders.minescribe.core.packinfo;

import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.nio.file.Path;
import java.util.Optional;

public abstract class PackContentType implements ILabeledValue, IFullName {
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
}
