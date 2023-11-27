package xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

public interface IPackContentNode {
    @NotNull
    Path getPath();

    @Nullable
    IPackContentNode getNode(Path path);

    @NotNull
    List<NodeTracker> getNodeTrackers();
}
