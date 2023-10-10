package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.mojang.datafixers.util.Unit;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class PackContentTypeRegistry {
    private static final PackContentTypeRegistry INSTANCE = new PackContentTypeRegistry();

    private final CompletableFuture<Unit> packContentTypesLoaded;
    private final CompletableFuture<Unit> packContentSubTypesLoaded;

    private final Map<ResourceId, PackContentType> packContentTypes;
    private final Map<ResourceId, List<PackContentType>> packContentSubTypes;

    public PackContentTypeRegistry() {
        this.packContentTypesLoaded = new CompletableFuture<>();
        this.packContentSubTypesLoaded = new CompletableFuture<>();

        this.packContentTypes = new HashMap<>();
        this.packContentSubTypes = new HashMap<>();
    }

    public void setPackContentTypes(List<PackContentType> packContentTypes) {
        this.packContentTypes.clear();
        for (PackContentType packContentType : packContentTypes) {
            this.packContentTypes.put(
                    packContentType.resourceId(),
                    packContentType
            );
        }
        this.packContentTypesLoaded.complete(Unit.INSTANCE);
    }

    public List<PackContentType> getPackContentTypes() {
        return new ArrayList<>(this.packContentTypes.values());
    }

    public List<PackContentType> getPackContentSubTypes(PackContentType packContentType) {
        return this.packContentSubTypes.get(packContentType.resourceId());
    }

    public void setPackContentSubTypes(Map<ResourceId, List<PackContentType>> packContentSubTypes) {
        this.packContentSubTypes.clear();
        for (Entry<ResourceId, List<PackContentType>> packContentSubType : packContentSubTypes.entrySet()) {

            this.packContentSubTypes.put(
                    packContentSubType.getKey(),
                    new ArrayList<>(packContentSubType.getValue())
            );
        }
        this.packContentSubTypesLoaded.complete(Unit.INSTANCE);
    }

    public CompletableFuture<Unit> getPackContentTypesLoaded() {
        return this.packContentTypesLoaded;
    }

    public CompletableFuture<Unit> getPackContentSubTypesLoaded() {
        return packContentSubTypesLoaded;
    }

    public static PackContentTypeRegistry getInstance() {
        return INSTANCE;
    }
}
