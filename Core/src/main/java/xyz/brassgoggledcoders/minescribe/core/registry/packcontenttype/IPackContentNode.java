package xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;

import java.nio.file.Path;
import java.util.List;

public interface IPackContentNode {
    @NotNull
    Path getPath();

    @Nullable
    IPackContentNode getNode(Path path);

    List<PackContentType> getTypes();
}
