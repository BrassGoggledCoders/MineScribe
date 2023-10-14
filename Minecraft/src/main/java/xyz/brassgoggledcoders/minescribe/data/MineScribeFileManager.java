package xyz.brassgoggledcoders.minescribe.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import xyz.brassgoggledcoders.minescribe.MineScribe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MineScribeFileManager {
    private final Path mineScribeRoot;
    private final Gson gson;

    public MineScribeFileManager(Path minecraftRoot) {
        this.mineScribeRoot = minecraftRoot.resolve(".minescribe");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public Path getMineScribeRoot() {
        return mineScribeRoot;
    }

    public <T> void writeFile(Path path, Codec<T> codec, T value) {
        if (!path.isAbsolute() || path.startsWith(this.mineScribeRoot)) {
            codec.encodeStart(JsonOps.INSTANCE, value)
                    .get()
                    .ifLeft(result -> writeToFile(path, result))
                    .ifRight(partial -> MineScribe.LOGGER.error("Failed to serialize {}, due to {}", value, partial.message()));
        } else {
            MineScribe.LOGGER.error("Not writing file {} as it is outside MineScribe's path", path);
        }
    }

    private void writeToFile(Path path, JsonElement jsonElement) {
        try {
            Path properPath = this.mineScribeRoot.resolve(path);
            Files.createDirectories(properPath.getParent());
            Files.writeString(
                    properPath,
                    this.gson.toJson(jsonElement),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            MineScribe.LOGGER.error("Failed to write file {}", path, e);
        }
    }
}
