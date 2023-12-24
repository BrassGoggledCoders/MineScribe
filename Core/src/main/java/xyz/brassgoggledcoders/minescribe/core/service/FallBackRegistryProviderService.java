package xyz.brassgoggledcoders.minescribe.core.service;

import xyz.brassgoggledcoders.minescribe.core.MineScribeRuntime;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicStaticRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FallBackRegistryProviderService implements IRegistryProviderService {
    public static Registry<ResourceId, MineScribePackType> PACK_TYPES = new BasicStaticRegistry<>(
            RegistryNames.PACK_TYPES,
            ResourceId.CODEC,
            register -> {

            }
    );

    public static Registry<ResourceId, PackContentParentType> PARENT_TYPES = new BasicStaticRegistry<>(
            RegistryNames.CONTENT_PARENT_TYPES,
            ResourceId.CODEC,
            register -> {

            }
    );

    public static Registry<ResourceId, PackContentChildType> CHILD_TYPES = new BasicStaticRegistry<>(
            RegistryNames.CONTENT_CHILD_TYPES,
            ResourceId.CODEC,
            register -> {

            }
    );

    public static Registry<ResourceId, ObjectType> OBJECT_TYPES = new BasicStaticRegistry<>(
            RegistryNames.OBJECT_TYPES,
            ResourceId.CODEC,
            register -> {

            }
    );

    public static Registry<ResourceId, FormList> FORM_LISTS = new BasicStaticRegistry<>(
            RegistryNames.FORM_LIST_VALUES,
            ResourceId.CODEC,
            register -> {

            }
    );

    private final List<Registry<?, ?>> registries;

    public FallBackRegistryProviderService() {
        this.registries = List.of(
                PACK_TYPES,
                PARENT_TYPES,
                CHILD_TYPES,
                OBJECT_TYPES,
                FORM_LISTS
        );
    }

    @Override
    public Collection<? extends Registry<?, ?>> getRegistries() {
        return this.registries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        if (MineScribeRuntime.getRuntime() == MineScribeRuntime.MINECRAFT) {
            return this.getRegistries()
                    .stream()
                    .filter(registry -> registry.getId().equals(name))
                    .map(registry -> (Registry<K, V>) registry)
                    .findAny();
        }

        return Optional.empty();
    }

    @Override
    public int priority() {
        return 1;
    }
}
