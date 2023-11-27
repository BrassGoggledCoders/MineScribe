package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadOnGetJsonRegistry<V> extends Registry<ResourceId, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadOnGetJsonRegistry.class);
    private static final Gson GSON = new Gson();
    private final Codec<V> vCodec;
    private final Path basePath;

    private Path dataPath;

    public LoadOnGetJsonRegistry(String name, Path basePath, Codec<V> vCodec) {
        super(name, ResourceId.CODEC);
        this.basePath = basePath;
        this.vCodec = vCodec;
    }

    @Override
    public V getValue(ResourceId key) {
        if (!this.getMap().containsKey(key)) {
            this.load(key);
        }

        return super.getValue(key);
    }

    public void setDataPath(Path mineScribePath) {
        this.dataPath = mineScribePath;
    }

    private void load(ResourceId id) {
        if (this.dataPath != null) {
            Path filePath = this.dataPath.resolve(id.namespace())
                    .resolve(this.basePath)
                    .resolve(id.path() + ".json");

            if (Files.isRegularFile(filePath)) {
                try {
                    String jsonString = Files.readString(filePath, StandardCharsets.UTF_8);
                    JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                    this.vCodec.decode(JsonOps.INSTANCE, jsonElement)
                            .get()
                            .ifLeft(result -> this.register(id, result.getFirst()))
                            .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", id, partial.message()));
                } catch (IOException e) {
                    LOGGER.error("Failed to load Value for file {}", id, e);
                }
            }
        } else {
            LOGGER.error("Failed to load List for Id {}", id);
        }
    }
}
