package xyz.brassgoggledcoders.minescribe.model;

import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;

public record PackType(
        TextComponent label,
        String folder
) {
}
