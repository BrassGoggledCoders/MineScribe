package xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackContentNode implements IPackContentNode {
    private final Path path;
    private final List<NodeTracker> nodeTrackers;
    private final Map<String, IPackContentNode> childNodes;

    public PackContentNode(Path path, List<NodeTracker> nodeTrackers) {
        this.path = path;
        this.nodeTrackers = nodeTrackers;
        this.childNodes = new HashMap<>();
    }

    @Override
    public @NotNull Path getPath() {
        return this.path;
    }

    @Override
    public IPackContentNode getNode(Path path) {
        String pathName = path.toString();
        IPackContentNode packContentNode = childNodes.get(pathName);
        if (packContentNode == null && !childNodes.containsKey(pathName)) {
            List<NodeTracker> childNodeTrackers = new ArrayList<>();
            for (NodeTracker nodeTracker : this.nodeTrackers) {
                childNodeTrackers.addAll(nodeTracker.advancePath(path));
            }
            if (!childNodeTrackers.isEmpty()) {
                packContentNode = new PackContentNode(path, childNodeTrackers);
            }

            childNodes.put(pathName, packContentNode);
        }
        return packContentNode;
    }

    @Override
    public @NotNull List<NodeTracker> getNodeTrackers() {
        return this.nodeTrackers;
    }
}
