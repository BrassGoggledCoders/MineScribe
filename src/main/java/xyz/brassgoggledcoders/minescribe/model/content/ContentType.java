package xyz.brassgoggledcoders.minescribe.model.content;

import xyz.brassgoggledcoders.minescribe.model.ProjectPath;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.model.pack.PackType;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.util.List;
import java.util.Optional;

public record ContentType(
        TextComponent label,
        Optional<RegistryHolder<ContentType>> category,
        ProjectPath path,
        List<RegistryHolder<PackType>> packTypes
) {
}
