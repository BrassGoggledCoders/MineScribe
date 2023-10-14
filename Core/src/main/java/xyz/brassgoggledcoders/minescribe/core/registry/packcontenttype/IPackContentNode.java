package xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface IPackContentNode {
    @NotNull
    Path getPath();

    @Nullable
    IPackContentNode getNode(Path path);
}
