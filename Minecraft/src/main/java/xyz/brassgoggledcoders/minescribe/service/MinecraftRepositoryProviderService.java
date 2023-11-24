package xyz.brassgoggledcoders.minescribe.service;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicStaticRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
import xyz.brassgoggledcoders.minescribe.util.PackTypeHelper;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class MinecraftRepositoryProviderService implements IRegistryProviderService {
    private final BasicStaticRegistry<String, MineScribePackType> packTypes = new BasicStaticRegistry<>(
            RegistryNames.PACK_TYPES,
            Codec.STRING,
            registry -> PackTypeHelper.gatherPackTypes()
                    .forEach(packType -> registry.accept(packType.name(), packType))
    );

    @Override
    public Collection<String> getRegistryNames() {
        return Collections.singleton(packTypes.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        if (name.equals(RegistryNames.PACK_TYPES)) {
            return Optional.of((Registry<K, V>) packTypes);
        }
        return Optional.empty();
    }

    @Override
    public void load(Path mineScribeRoot) {

    }

    @Override
    public void addSourcePath(Path sourcePath) {

    }
}
