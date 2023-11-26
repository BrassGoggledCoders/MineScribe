package xyz.brassgoggledcoders.minescribe.core.packinfo;

import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;

import java.nio.file.Path;
import java.util.Optional;

public class PackContentType implements ILabeledValue {
    private final String label;
    private final Path path;
    private final FileForm form;

    public PackContentType(String label, Path path, FileForm form) {
        this.label = label;
        this.path = path;
        this.form = form;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public Path getPath() {
        return path;
    }

    public Optional<FileForm> getForm() {
        return Optional.ofNullable(form);
    }
}
