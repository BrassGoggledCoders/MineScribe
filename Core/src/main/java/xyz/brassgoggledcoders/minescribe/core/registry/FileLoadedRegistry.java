package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashSet;
import java.util.Set;

public abstract class FileLoadedRegistry<K, V> extends Registry<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoadedRegistry.class);
    private final Set<Path> sourcePaths;
    private final String directory;
    private final String fileType;

    public FileLoadedRegistry(String name, Codec<K> kCodec, String directory, String fileType) {
        super(name, kCodec);
        this.sourcePaths = new HashSet<>();
        this.directory = directory.replace("/", File.separator)
                .replace("\\", File.separator);
        this.fileType = fileType;
    }

    protected abstract void handleFileInFolder(Path path, String fileName, String fileContents);

    public void load(Path sourcePath) {
        if (this.sourcePaths.add(sourcePath)) {
            String stringMatcher = "**" + File.separator +
                    this.directory + File.separator + "**." + this.fileType;
            stringMatcher = stringMatcher.replace("\\", "\\\\");

            PathMatcher matcher = sourcePath.getFileSystem()
                    .getPathMatcher("glob:" + stringMatcher);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
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
                        handleFileInFolder(path, fileName, jsonString);
                    } catch (IOException e) {
                        LOGGER.error("Failed to load Value for file {}", fileName, e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load Values for registry {}", this.getName(), e);
        }
    }

    @Nullable
    protected Path findSourcePath(@NotNull Path path) {
        return this.sourcePaths.stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);
    }
}
