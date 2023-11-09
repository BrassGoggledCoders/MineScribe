package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class BasicJsonRegistry<K, V> extends FileLoadedRegistry<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicJsonRegistry.class);
    private static final Gson GSON = new Gson();
    private final Codec<V> vCodec;
    private final Codec<List<V>> vCodecList;
    private final Function<V, K> valueName;

    public BasicJsonRegistry(String name, Path directory, Codec<K> kCodec, Codec<V> vCodec, Function<V, K> valueName) {
        super(name, kCodec, setPath(directory), "json");
        this.vCodec = vCodec;
        this.vCodecList = vCodec.listOf();
        this.valueName = valueName;
    }

    private static Path setPath(Path directory) {
        if (directory == null) {
            return null;
        } else {
            return Path.of("registry")
                    .resolve(directory);
        }
    }

    @Override
    protected void handleSingleFile(String fileContents) {
        JsonElement jsonElement = GSON.fromJson(fileContents, JsonElement.class);
        vCodecList.decode(JsonOps.INSTANCE, jsonElement)
                .get()
                .ifLeft(result -> result.getFirst().forEach(value -> this.register(valueName.apply(value), value)))
                .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", this.getName(), partial.message()));
    }

    @Override
    protected void handleFileInFolder(String fileName, String fileContents) {
        JsonElement jsonElement = GSON.fromJson(fileContents, JsonElement.class);
        this.vCodec.decode(JsonOps.INSTANCE, jsonElement)
                .get()
                .ifLeft(result -> this.register(valueName.apply(result.getFirst()), result.getFirst()))
                .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", fileName, partial.message()));

    }

    public static <V> BasicJsonRegistry<String, V> ofString(String name, Codec<V> vCodec, Function<V, String> valueName) {
        return new BasicJsonRegistry<>(
                name,
                null,
                Codec.STRING,
                vCodec,
                valueName
        );
    }
}
