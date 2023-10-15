package xyz.brassgoggledcoders.minescribe.core.packinfo;

import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

import java.nio.file.Path;
import java.util.Optional;

public class PackContentType {
    private final ResourceId id;
    private final String label;
    private final Path path;
    private final FileForm form;

    public PackContentType(ResourceId id, String label, Path path, FileForm form) {
        this.id = id;
        this.label = label;
        this.path = path;
        this.form = form;
    }

    public ResourceId getId() {
        return id;
    }

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
