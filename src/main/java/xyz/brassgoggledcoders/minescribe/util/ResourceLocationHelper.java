package xyz.brassgoggledcoders.minescribe.util;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocationHelper {
    public static String sanitize(ResourceLocation location) {
        return location.toString()
                .replace(":", "_")
                .replace("/", "_");
    }

    public static ResourceLocation prependPath(ResourceLocation location, String prepend) {
        return new ResourceLocation(
                location.getNamespace(),
                prepend + "/" + location.getPath()
        );
    }
}
