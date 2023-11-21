package xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;

import com.google.common.base.Suppliers;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PackContentHierarchy {
    private static final Supplier<PackContentHierarchy> INSTANCE = Suppliers.memoize(PackContentHierarchy::build);

    private final Map<MineScribePackType, RootPackContentNode> hierarchy;

    public PackContentHierarchy(Map<MineScribePackType, RootPackContentNode> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public IPackContentNode getNodeFor(MineScribePackType packType) {
        return hierarchy.get(packType);
    }


    public static PackContentHierarchy build() {
        Map<MineScribePackType, RootPackContentNode> hierarchy = new HashMap<>();
        for (MineScribePackType packType : Registries.getPackTypes()) {
            hierarchy.put(packType, new RootPackContentNode(packType));
        }
        return new PackContentHierarchy(hierarchy);
    }

    public static PackContentHierarchy getInstance() {
        return INSTANCE.get();
    }
}
