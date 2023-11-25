package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.nio.file.Path;
import java.util.function.Function;

public class BasicJsonRegistry<V> extends FileLoadedRegistry<ResourceId, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicJsonRegistry.class);
    private static final Gson GSON = new Gson();
    private final Codec<V> vCodec;

    public BasicJsonRegistry(String name, String directory, Codec<V> vCodec) {
        super(name, ResourceId.CODEC, directory, "json");
        this.vCodec = vCodec;
    }

    @Override
    protected void handleFileInFolder(Path path, ResourceId id, String fileContents) {
        JsonElement jsonElement = GSON.fromJson(fileContents, JsonElement.class);
        this.vCodec.decode(JsonOps.INSTANCE, jsonElement)
                .get()
                .ifLeft(result -> this.register(id, result.getFirst()))
                .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", path, partial.message()));

    }
}
