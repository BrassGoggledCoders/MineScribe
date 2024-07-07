package xyz.brassgoggledcoders.minescribe.registry;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public record RegistryRoot(
        Path path,
        int priority
) implements Comparable<RegistryRoot> {
    @Override
    public int compareTo(@NotNull RegistryRoot o) {
        return Integer.compare(priority, o.priority);
    }
}
