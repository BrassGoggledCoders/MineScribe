package xyz.brassgoggledcoders.minescribe.registry;

import java.util.Locale;

public record RegistryId(
        String namespace,
        String path
) {
    public RegistryId(
            String registryId
    ) {
        this(
                !registryId.contains(":") ? "minescribe" : registryId.substring(0, registryId.indexOf(":")),
                registryId.substring(registryId.indexOf(":") + 1)
        );
    }

    public RegistryId(String namespace, String path) {
        this.namespace = namespace.toLowerCase(Locale.US);
        this.path = path.toLowerCase(Locale.US);
    }
}
