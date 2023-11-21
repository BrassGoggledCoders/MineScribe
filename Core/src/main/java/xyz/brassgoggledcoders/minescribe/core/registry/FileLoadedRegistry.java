package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public abstract class FileLoadedRegistry<K, V> extends Registry<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoadedRegistry.class);
    private final String directory;
    private final String fileType;

    public FileLoadedRegistry(String name, Codec<K> kCodec, String directory, String fileType) {
        super(name, kCodec);
        this.directory = directory.replace("/", File.separator)
                .replace("\\", File.separator);
        this.fileType = fileType;
    }

    protected abstract void handleFileInFolder(String fileName, String fileContents);

    public void load(Path root) {
        String stringMatcher = "**" + File.separator + "data" + File.separator + "**" + File.separator +
                this.directory + File.separator + "**." + this.fileType;
        stringMatcher = stringMatcher.replace("\\", "\\\\");

        PathMatcher matcher = root.getFileSystem()
                .getPathMatcher("glob:" + stringMatcher);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    readFolder(path, matcher);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load Values for registry {}", this.getName(), e);
        }
        LOGGER.info("Loaded {} values for registry {}", this.getMap().size(), this.getName());
    }

    private void readFolder(Path parent, PathMatcher matcher) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, Files::exists)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    readFolder(path, matcher);
                } else if (matcher.matches(path)) {
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
