package xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PackContentHierarchy {
    private static final Supplier<PackContentHierarchy> INSTANCE = Suppliers.memoize(PackContentHierarchy::new);
    private final Map<RootInfo, RootPackContentNode> hierarchy;

    public PackContentHierarchy() {
        this.hierarchy = new IdentityHashMap<>();
    }

    public IPackContentNode getNodeFor(RootInfo rootInfo) {
        RootPackContentNode rootTypeNode = this.hierarchy.get(rootInfo);
        if (rootTypeNode == null) {
            rootTypeNode = new RootPackContentNode(rootInfo);
            this.hierarchy.put(rootInfo, rootTypeNode);
        }

        return rootTypeNode;
    }

    public static PackContentHierarchy getInstance() {
        return INSTANCE.get();
    }
}
