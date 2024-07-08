package xyz.brassgoggledcoders.minescribe.model.pack;

import xyz.brassgoggledcoders.minescribe.model.ProjectPath;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.util.List;

public record PackRepository(
        TextComponent label,
        ProjectPath path,
        List<RegistryHolder<PackType>> packTypes
) {
}
