package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

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

    protected abstract void handleFileInFolder(Path path, ResourceId id, String fileContents);

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
                    String fileName = parent.relativize(path).toString();
                    try {
                        String jsonString = Files.readString(path, StandardCharsets.UTF_8);
                        ResourceId id = getResourceId(path);
                        if (id != null) {
                            handleFileInFolder(path, id, jsonString);
                        } else {
                            LOGGER.error("Failed to convert {} to an id", path);
                        }
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
    private ResourceId getResourceId(Path path) {
        Path sourceRoot = null;
        for (Path testingRoot : this.sourcePaths) {
            if (path.startsWith(testingRoot)) {
                sourceRoot = testingRoot;
                break;
            }
        }
        ResourceId id = null;
        if (sourceRoot != null) {
            String relativePath = sourceRoot.relativize(path).toString().replace("." + this.fileType, "");
            int directoryIndex = relativePath.indexOf(this.directory);
            if (directoryIndex > 0) {
                String[] parts = relativePath.split(directory.replace("\\", "\\\\"));
                if (parts.length == 2) {
                    id = new ResourceId(
                            parts[0].substring(0, parts[0].length() - 1),
                            parts[1].substring(1)
                    );
                }
            }
        }
        return id;
    }

    @Nullable
    protected Path findSourcePath(@NotNull Path path) {
        return this.sourcePaths.stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);
    }
}
