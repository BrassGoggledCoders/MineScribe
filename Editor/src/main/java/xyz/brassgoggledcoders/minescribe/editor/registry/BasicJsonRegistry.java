package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Function;

public class BasicJsonRegistry<K, V> extends FileLoadedRegistry<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicJsonRegistry.class);
    private static final Gson GSON = new Gson();
    private final Codec<V> vCodec;

    public BasicJsonRegistry(String name, String directory, Codec<K> kCodec, Codec<V> vCodec) {
        super(name, kCodec, directory, "json");
        this.vCodec = vCodec;
    }

    @Override
    protected void handleFileInFolder(Path path, String fileName, String fileContents) {
        JsonElement jsonElement = GSON.fromJson(fileContents, JsonElement.class);
        this.vCodec.decode(JsonOps.INSTANCE, jsonElement)
                .get()
                .ifLeft(result -> this.register(valueName.apply(result.getFirst()), result.getFirst()))
                .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", fileName, partial.message()));

    }

    public static <V> BasicJsonRegistry<String, V> ofString(String name, String directory, Codec<V> vCodec, Function<V, String> valueName) {
        return new BasicJsonRegistry<>(
                name,
                directory,
                Codec.STRING,
                vCodec,
                valueName
        );
    }
}
