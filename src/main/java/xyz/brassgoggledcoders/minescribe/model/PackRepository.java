package xyz.brassgoggledcoders.minescribe.model;

import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.util.List;

public record PackRepository(
        TextComponent label,
        ProjectPath path,
        List<RegistryHolder<PackType>> packTypes
) {
}
