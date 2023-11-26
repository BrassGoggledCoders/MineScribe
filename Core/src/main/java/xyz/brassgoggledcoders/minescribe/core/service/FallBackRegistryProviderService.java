package xyz.brassgoggledcoders.minescribe.core.service;

import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicStaticRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class FallBackRegistryProviderService implements IRegistryProviderService {
    public static Registry<ResourceId, MineScribePackType> PACK_TYPES = new BasicStaticRegistry<>(
            RegistryNames.PACK_TYPES,
            ResourceId.CODEC,
            register -> {

            }
    );

    @Override
    public Collection<String> getRegistryNames() {
        return Collections.singleton(PACK_TYPES.getName());
    }

    @Override
    public Collection<? extends Registry<?, ?>> getRegistries() {
        return Collections.singleton(PACK_TYPES);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        if (name.equals(PACK_TYPES.getName())) {
            return Optional.of((Registry<K, V>) PACK_TYPES);
        }
        return Optional.empty();
    }

    @Override
    public int priority() {
        return 1;
    }
}
