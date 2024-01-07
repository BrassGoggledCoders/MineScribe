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

    private static final Gson GSON = new Gson();
    private final Logger logger;
    private final Codec<V> vCodec;

    public BasicJsonRegistry(String name, String directory, Codec<V> vCodec) {
        this(name, directory, vCodec, null);
    }

    public BasicJsonRegistry(String name, String directory, Codec<V> vCodec, Function<V, String> aliasFunction) {
        super(name, ResourceId.CODEC, directory, "json", aliasFunction);
        this.logger = LoggerFactory.getLogger(name + " registry");
        this.vCodec = vCodec;
    }

    @Override
    protected int handleFileInFolder(Path path, ResourceId id, String fileContents) {
        JsonElement jsonElement = GSON.fromJson(fileContents, JsonElement.class);
        return this.vCodec.decode(JsonOps.INSTANCE, jsonElement)
                .get()
                .mapLeft(result -> this.register(id, result.getFirst()) ? 1 : 0)
                .ifRight(partial -> logger.error("Failed to decode file {} due to {}", path, partial.message()))
                .left()
                .orElse(0);
    }
}
