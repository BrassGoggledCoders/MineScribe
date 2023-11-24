package xyz.brassgoggledcoders.minescribe.core.service;

import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface IRegistryProviderService {
    Collection<String> getRegistryNames();

    <K, V> Optional<Registry<K, V>> getRegistry(String name);

    default void load(Path mineScribeRoot) {

    }

    default void addSourcePath(Path sourcePath) {

    }

    default void validate() {
        for (String name : this.getRegistryNames()) {
            this.getRegistry(name)
                    .ifPresent(Registry::validate);
        }
    }
}
