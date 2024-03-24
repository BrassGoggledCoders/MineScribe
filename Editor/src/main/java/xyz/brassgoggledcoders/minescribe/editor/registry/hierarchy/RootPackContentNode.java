package xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;

import com.google.common.base.Suppliers;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.registry.Holder;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class RootPackContentNode implements IPackContentNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootPackContentNode.class);
    private static final Path PATH = Path.of(".");
    private final RootInfo rootInfo;
    private final Supplier<List<Holder<ResourceId, PackContentType>>> collectParents;
    private final Supplier<List<NodeTracker>> collectNodeTrackers;
    private final Map<String, IPackContentNode> nodes;

    public RootPackContentNode(RootInfo rootInfo) {
        this.rootInfo = rootInfo;
        this.collectParents = Suppliers.memoize(this::collectParents);
        this.collectNodeTrackers = Suppliers.memoize(this::collectNodeTrackers);
        this.nodes = new HashMap<>();
    }

    @Override
    public @NotNull Path getPath() {
        return PATH;
    }

    @Override
    public IPackContentNode getNode(Path path) {
        if (path.isAbsolute() || path.getNameCount() > 1) {
            LOGGER.error("Found Absolute or Longer Path {}", path);
            return null;
        } else {
            IPackContentNode contentNode = this.nodes.get(path.toString());
            if (contentNode == null && !this.nodes.containsKey(path.toString())) {
                List<NodeTracker> nodeTrackers = new ArrayList<>();
                for (NodeTracker nodeTracker : this.collectNodeTrackers.get()) {
                    nodeTrackers.addAll(nodeTracker.advancePath(path));
                }
                if (!nodeTrackers.isEmpty()) {
                    contentNode = new PackContentNode(path, nodeTrackers);
                }
                this.nodes.put(path.toString(), contentNode);
            }
            return contentNode;
        }
    }

    @Override
    public @NotNull List<NodeTracker> getNodeTrackers() {
        return this.collectNodeTrackers.get();
    }

    private List<NodeTracker> collectNodeTrackers() {
        List<NodeTracker> nodeTrackers = new ArrayList<>(this.collectParents.get().size());
        for (Holder<ResourceId, PackContentType> parentType : this.collectParents.get()) {
            nodeTrackers.add(new NodeTracker(parentType, Optional.empty(), 0));
        }
        return nodeTrackers;
    }

    private List<Holder<ResourceId, PackContentType>> collectParents() {
        List<Holder<ResourceId, PackContentType>> packContentParentTypes = new ArrayList<>();
        for (Holder<ResourceId, PackContentType> parentType : EditorRegistries.getContentParentTypes().getHolders()) {
            if (parentType.exists(type -> type.getRootInfo().equals(this.rootInfo))) {
                packContentParentTypes.add(parentType);
            }
        }
        return packContentParentTypes;
    }
}
