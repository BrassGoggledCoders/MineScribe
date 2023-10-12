package xyz.brassgoggledcoders.minescribe.list;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.registries.RegistryManager;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.list.IListProvider;
import xyz.brassgoggledcoders.minescribe.core.fileform.listhandler.IListHandler;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

public class GsonListHandler implements IListHandler {
    public static WeakReference<MinecraftServer> minecraftServerWeakReference = new WeakReference<>(null);
    private final Map<UUID, List<String>> identifiedLists;
    private final BiMap<IListProvider, UUID> existingListProviders;

    private final Codec<IListProvider> dispatchCodec;

    public GsonListHandler() {
        this.identifiedLists = new IdentityHashMap<>();
        this.existingListProviders = HashBiMap.create();
        this.dispatchCodec = RegistryManager.ACTIVE.getRegistry(MineScribe.KEY)
                .getCodec()
                .dispatch(
                        IListProvider::codec,
                        Function.identity()
                );

    }

    @Override
    public UUID createAndStoreList(JsonElement listObject) throws JsonParseException {
        IListProvider listProvider = dispatchCodec.decode(JsonOps.INSTANCE, listObject)
                .result()
                .map(Pair::getFirst)
                .orElseGet(EmptyListProvider::new);

        UUID id = UUID.randomUUID();
        existingListProviders.put(listProvider, id);
        return id;
    }

    @Override
    public Pair<List<UUID>, List<String>> retrieveList(UUID id) {
        List<String> values = identifiedLists.get(id);
        if (values == null) {
            IListProvider listProvider = this.existingListProviders.inverse().get(id);
            MinecraftServer minecraftServer = minecraftServerWeakReference.get();
            if (listProvider != null) {
                if (minecraftServer != null) {
                    Pair<List<UUID>, List<String>> results = listProvider.provideList(minecraftServer.registryAccess());
                    this.identifiedLists.put(id, results.getSecond());
                    return results;
                }
            } else {
                values = Collections.emptyList();
                this.identifiedLists.put(id, values);
            }
        }
        return Pair.of(Collections.emptyList(), values);
    }
}
