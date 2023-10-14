package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Unit;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class PackContentTypeRegistry {
    private static final PackContentTypeRegistry INSTANCE = new PackContentTypeRegistry();

    private final CompletableFuture<Unit> packContentTypesLoaded;
    private final CompletableFuture<Unit> packContentSubTypesLoaded;

    private final Map<ResourceId, PackContentType> packContentTypes;
    private final Map<ResourceId, List<PackContentType>> packContentSubTypes;
    private final Multimap<MineScribePackType, PackContentType> packContentTypeByPackType;

    public PackContentTypeRegistry() {
        this.packContentTypesLoaded = new CompletableFuture<>();
        this.packContentSubTypesLoaded = new CompletableFuture<>();

        this.packContentTypes = new HashMap<>();
        this.packContentSubTypes = new HashMap<>();
        this.packContentTypeByPackType = HashMultimap.create();
    }

    public void setPackContentTypes(List<PackContentType> packContentTypes) {
        this.packContentTypes.clear();
        for (PackContentType packContentType : packContentTypes) {
            this.packContentTypes.put(
                    packContentType.getId(),
                    packContentType
            );
        }
        this.packContentTypesLoaded.complete(Unit.INSTANCE);
    }

    public List<PackContentType> getPackContentTypes() {
        return new ArrayList<>(this.packContentTypes.values());
    }

    public List<PackContentType> getPackContentSubTypes(PackContentType packContentType) {
        return this.packContentSubTypes.get(packContentType.getId());
    }

    public void setPackContentSubTypes(Map<ResourceId, Collection<PackContentType>> packContentSubTypes) {
        this.packContentSubTypes.clear();
        for (Entry<ResourceId, Collection<PackContentType>> packContentSubType : packContentSubTypes.entrySet()) {
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

    public Collection<PackContentType> getPackContentTypesFor(MineScribePackType packType) {
        if (packContentTypeByPackType.containsKey(packType)) {
            return packContentTypeByPackType.get(packType);
        } else {
            List<PackContentType> packContentTypesFor = new ArrayList<>();
            //TODO Rebuild PackContentType dependencies
            for (PackContentType packContentType : packContentTypes.values()) {
                //if (packContentType.packType().equalsIgnoreCase(packType.name())) {
                //    packContentTypesFor.add(packContentType);
                //}
            }

            return packContentTypesFor;
        }
    }

    public static PackContentTypeRegistry getInstance() {
        return INSTANCE;
    }
}
