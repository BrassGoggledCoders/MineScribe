package xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;

import java.nio.file.Path;
import java.util.List;

public class ParentPackContentNode implements IPackContentNode {
    private final Path path;
    private final List<PackContentParentType> contentTypes;

    public ParentPackContentNode(Path path, List<PackContentParentType> contentTypes) {
        this.path = path;
        this.contentTypes = contentTypes;
    }

    @Override
    public @NotNull Path getPath() {
        return this.path;
    }

    @Override
    public IPackContentNode getNode(Path path) {
        return null;
    }
}
