package xyz.brassgoggledcoders.minescribe.pack;

import com.mojang.datafixers.util.Function3;

import java.nio.file.Path;

public enum PackLocation {
    WORLD((mcFolder, worldFolder, packType) -> worldFolder.resolve("datapacks")),
    RESOURCE_PACKS((mcFolder, worldFolder, packType) -> mcFolder.resolve("resourcepacks")),
    OPEN_LOADER((mcFolder, worldFolder, packType) -> {
        Path openLoader = mcFolder.resolve("config/openloader");
        if (packType == MSPackType.RESOURCE) {
            return openLoader.resolve("resources");
        } else {
            return openLoader.resolve("data");
        }
    });

    private final Function3<Path, Path, MSPackType, Path> pathResolver;

    PackLocation(Function3<Path, Path, MSPackType, Path> pathResolver) {
        this.pathResolver = pathResolver;
    }

    public Path resolve(MSPackType packType, Path gameFolder, Path worldFolder) {
        return pathResolver.apply(gameFolder, worldFolder, packType);
    }
}
