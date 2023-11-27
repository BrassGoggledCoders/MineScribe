package xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;

import com.google.common.base.Suppliers;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class RootPackContentNode implements IPackContentNode {
    private static final Path PATH = Path.of(".");
    private final MineScribePackType mineScribePackType;
    private final Supplier<List<PackContentParentType>> collectParents;
    private final Supplier<List<NodeTracker>> collectNodeTrackers;
    private final Map<String, IPackContentNode> nodes;

    public RootPackContentNode(MineScribePackType mineScribePackType) {
        this.mineScribePackType = mineScribePackType;
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
        for (PackContentParentType parentType : this.collectParents.get()) {
            nodeTrackers.add(new NodeTracker(parentType, Optional.empty(), 0));
        }
        return nodeTrackers;
    }

    private List<PackContentParentType> collectParents() {
        List<PackContentParentType> packContentParentTypes = new ArrayList<>();
        for (PackContentParentType parentType : EditorRegistries.getContentParentTypes()) {
            if (parentType.getPackType() == mineScribePackType) {
                packContentParentTypes.add(parentType);
            }
        }
        return packContentParentTypes;
    }
}
