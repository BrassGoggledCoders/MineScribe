package xyz.brassgoggledcoders.minescribe.model;

import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.nio.file.Path;
import java.util.List;

public record Pack(
        Path path,
        List<RegistryHolder<PackType>> packTypes
) {
}
