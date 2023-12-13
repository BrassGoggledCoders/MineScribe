package xyz.brassgoggledcoders.minescribe.core.service;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface IRegistryProviderService extends Comparable<IRegistryProviderService> {
    Collection<? extends Registry<?, ?>> getRegistries();

    <K, V> Optional<Registry<K, V>> getRegistry(String name);

    default int priority() {
        return 0;
    }

    default void load(Path mineScribeRoot) {

    }

    default void addSourcePath(Path sourcePath) {

    }

    default void validate() {
        for (Registry<?, ?> registry : this.getRegistries()) {
            registry.validate();
        }
    }

    @Override
    default int compareTo(@NotNull IRegistryProviderService o) {
        return Integer.compare(this.priority(), o.priority());
    }
}
