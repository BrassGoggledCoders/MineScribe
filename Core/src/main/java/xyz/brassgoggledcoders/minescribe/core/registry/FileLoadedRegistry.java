package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileLoadedRegistry<K, V> extends Registry<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoadedRegistry.class);
    private final Path directory;
    private final String fileType;

    public FileLoadedRegistry(String name, Codec<K> kCodec, Path directory, String fileType) {
        super(name, kCodec);
        this.directory = directory;
        this.fileType = fileType;
    }

    protected abstract void handleSingleFile(String fileContents);

    protected abstract void handleFileInFolder(String fileName, String fileContents);

    public void load(Path root) {
        Path registryPath = root.resolve("registry");
        if (this.directory == null) {
            registryPath = registryPath.resolve(this.getName());
        } else {
            registryPath = registryPath.resolve(this.directory);
        }

        if (Files.isDirectory(registryPath)) {
            readFolder(registryPath);
        } else {
            Path filePath = root.resolve("registry")
                    .resolve(this.getName() + "." + fileType);

            if (Files.exists(filePath)) {
                try {
                    handleSingleFile(Files.readString(filePath, StandardCharsets.UTF_8));
                } catch (IOException e) {
                    LOGGER.error("Failed to load Value for file {}", this.getName(), e);
                }
            } else {
                LOGGER.error("Failed to load any values for registry {}", this.getName());
            }
        }

        LOGGER.info("Loaded {} values for registry {}", this.getMap().size(), this.getName());
    }

    private void readFolder(Path parent) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, Files::exists)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    readFolder(path);
                } else {
                    String fileName = path.relativize(parent).toString();
                    try {
                        String jsonString = Files.readString(path, StandardCharsets.UTF_8);
                        handleFileInFolder(fileName, jsonString);
                    } catch (IOException e) {
                        LOGGER.error("Failed to load Value for file {}", fileName, e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load Values for registry {}", this.getName(), e);
        }
    }
}
