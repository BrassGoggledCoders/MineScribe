package xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;

import com.google.common.base.Suppliers;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class RootPackContentNode implements IPackContentNode {
    private static final Path PATH = Path.of(".");
    private final MineScribePackType mineScribePackType;
    private final Supplier<List<PackContentParentType>> collectParents;
    private final Map<String, IPackContentNode> nodes;

    public RootPackContentNode(MineScribePackType mineScribePackType) {
        this.mineScribePackType = mineScribePackType;
        this.collectParents = Suppliers.memoize(this::collectParents);
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
                for (PackContentParentType packContentType : this.collectParents.get()) {
                    if (packContentType.getPath().startsWith(path)) {
                        nodeTrackers.add(new NodeTracker(packContentType, Optional.empty(), 1));
                    }
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
        return Collections.emptyList();
    }

    private List<PackContentParentType> collectParents() {
        List<PackContentParentType> packContentParentTypes = new ArrayList<>();
        for (PackContentParentType parentType : Registries.getContentParentTypes()) {
            if (parentType.getPackType() == mineScribePackType) {
                packContentParentTypes.add(parentType);
            }
        }
        return packContentParentTypes;
    }
}
