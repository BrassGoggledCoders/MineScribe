package xyz.brassgoggledcoders.minescribe.model.pack.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PackMetaDataContainer(
        @JsonProperty("pack") Optional<PackMetaData> pack
) {

}
