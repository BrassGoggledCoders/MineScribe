package xyz.brassgoggledcoders.minescribe.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.apache.commons.io.FileUtils;
import xyz.brassgoggledcoders.minescribe.MineScribe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MineScribeFileManager {
    private final Path mineScribeRoot;
    private final Path mineScribeData;
    private final Gson gson;

    public MineScribeFileManager(Path minecraftRoot) {
        this.mineScribeRoot = minecraftRoot.resolve(".minescribe");
        this.mineScribeData = this.mineScribeRoot.resolve("data");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public Path getMineScribeRoot() {
        return mineScribeRoot;
    }

    public <T> void writeFile(Path path, Codec<T> codec, T value) {
        if (!path.isAbsolute() || path.startsWith(this.mineScribeData)) {
            codec.encodeStart(JsonOps.INSTANCE, value)
                    .get()
                    .ifLeft(result -> writeToFile(path, result))
                    .ifRight(partial -> MineScribe.LOGGER.error("Failed to serialize {}, due to {}", value, partial.message()));
        } else {
            MineScribe.LOGGER.error("Not writing file {} as it is outside MineScribe's path", path);
        }
    }

    public void writeFile(Path path, String contents) {
        if (!path.isAbsolute() || path.startsWith(this.mineScribeRoot)) {
            writeToFile(path, contents);
        } else {
            MineScribe.LOGGER.error("Not writing file {} as it is outside MineScribe's path", path);
        }
    }

    private void writeToFile(Path path, String value) {
        try {
            Path properPath = this.mineScribeData.resolve(path);
            Files.createDirectories(properPath.getParent());
            Files.writeString(
                    properPath,
                    value,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            MineScribe.LOGGER.error("Failed to write file {}", path, e);
        }
    }

    private void writeToFile(Path path, JsonElement jsonElement) {
        writeToFile(path, this.gson.toJson(jsonElement));
    }

    public void clearRoot() {
        try {
            FileUtils.deleteDirectory(this.getMineScribeRoot().toFile());
        } catch (IOException e) {
            MineScribe.LOGGER.error("Failed to Delete .minescribe folder before generation", e);
        }
    }
}
