package xyz.brassgoggledcoders.minescribe.core.packinfo;

import java.nio.file.Path;

public record PackContentType(
        ResourceId resourceId,
        String localization,
        Path path
) {

}
