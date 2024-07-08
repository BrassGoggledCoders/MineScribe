package xyz.brassgoggledcoders.minescribe.model.pack;

import xyz.brassgoggledcoders.minescribe.model.component.LiteralTextContent;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.model.pack.metadata.PackMetaData;
import xyz.brassgoggledcoders.minescribe.model.pack.metadata.PackMetaDataContainer;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.nio.file.Path;
import java.util.List;

public record Pack(
        Path path,
        PackMetaDataContainer metaData,
        List<RegistryHolder<PackType>> packTypes
) {
    public TextComponent getDescription() {
        return this.metaData()
                .pack()
                .map(PackMetaData::description)
                .orElseGet(() -> new TextComponent(new LiteralTextContent("")));
    }
}
