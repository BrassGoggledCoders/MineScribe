package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class BasicJsonRegistry<V> extends Registry<String, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicJsonRegistry.class);
    private static final Gson GSON = new Gson();
    private final Codec<V> vCodec;
    private final Function<V, String> valueName;

    public BasicJsonRegistry(String name, Codec<V> vCodec, Function<V, String> valueName) {
        super(name, Codec.STRING);
        this.vCodec = vCodec;
        this.valueName = valueName;
    }

    public void load(Path root) {
        Path registryPath = root.resolve("registry")
                .resolve(this.getName());

        if (Files.isDirectory(registryPath)) {
            DirectoryStream.Filter<Path> filter = file -> file.endsWith(".json") && Files.isReadable(file);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(registryPath, filter)) {
                for (Path path : stream) {
                    String name = path.relativize(registryPath).toString();
                    try {
                        String jsonString = Files.readString(path, StandardCharsets.UTF_8);
                        JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                        this.vCodec.decode(JsonOps.INSTANCE, jsonElement)
                                .get()
                                .ifLeft(result -> this.register(name, result.getFirst()))
                                .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", name, partial.message()));
                    } catch (IOException e) {
                        LOGGER.error("Failed to load Value for file {}", name, e);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load Values for registry {}", this.getName(), e);
            }


        } else {
            Path filePath = root.resolve("registry")
                    .resolve(this.getName() + ".json");

            if (Files.exists(filePath)) {
                Codec<List<V>> listCodec = this.vCodec.listOf();
                try {
                    String jsonString = Files.readString(filePath, StandardCharsets.UTF_8);
                    JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                    listCodec.decode(JsonOps.INSTANCE, jsonElement)
                            .get()
                            .ifLeft(result -> result.getFirst().forEach(value -> this.register(valueName.apply(value), value)))
                            .ifRight(partial -> LOGGER.error("Failed to decode file {} due to {}", this.getName(), partial.message()));
                } catch (IOException e) {
                    LOGGER.error("Failed to load Value for file {}", this.getName(), e);
                }
            } else {
                LOGGER.error("Failed to load any values for registry {}", this.getName());
            }
        }

        LOGGER.info("Loaded {} values for registry {}", this.getMap().size(), this.getName());
    }
}
