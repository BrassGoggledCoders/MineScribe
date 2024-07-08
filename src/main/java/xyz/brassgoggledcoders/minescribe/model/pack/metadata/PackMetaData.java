package xyz.brassgoggledcoders.minescribe.model.pack.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;

public record PackMetaData(
        @JsonProperty("description") TextComponent description,
        @JsonProperty("pack_format") int packFormat
) {
}
