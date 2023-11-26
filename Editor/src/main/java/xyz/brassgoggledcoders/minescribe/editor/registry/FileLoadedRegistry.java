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
    private final Logger logger;
    private final Set<Path> sourcePaths;
    private final String directory;
    private final String fileType;

    public FileLoadedRegistry(String name, Codec<K> kCodec, String directory, String fileType) {
        super(name, kCodec);
        this.logger = LoggerFactory.getLogger(name + " registry");
        this.sourcePaths = new HashSet<>();
        this.directory = directory.replace("/", File.separator)
                .replace("\\", File.separator);
        this.fileType = fileType;
    }

    protected abstract int handleFileInFolder(Path path, ResourceId id, String fileContents);

    public void load(Path sourcePath) {
        if (this.sourcePaths.add(sourcePath)) {
            String stringMatcher = "**" + File.separator +
                    this.directory + File.separator + "**." + this.fileType;
            stringMatcher = stringMatcher.replace("\\", "\\\\");

            PathMatcher matcher = sourcePath.getFileSystem()
                    .getPathMatcher("glob:" + stringMatcher);

            int loaded = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        loaded += readFolder(path, matcher);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to load Values for registry {}", this.getName(), e);
            }
            logger.info("Loaded {} values for registry {} from {}", loaded, this.getName(), sourcePath);
        }
    }

    private int readFolder(Path parent, PathMatcher matcher) {
        int loaded = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, Files::exists)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    loaded += readFolder(path, matcher);
                } else if (matcher.matches(path)) {
                    String fileName = parent.relativize(path).toString();
                    try {
                        String jsonString = Files.readString(path, StandardCharsets.UTF_8);
                        ResourceId id = getResourceId(path);
                        if (id != null) {
                            loaded += handleFileInFolder(path, id, jsonString);
                        } else {
                            logger.error("Failed to convert {} to an id", path);
                        }
                    } catch (IOException e) {
                        logger.error("Failed to load Value for file {}", fileName, e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load Values for registry {}", this.getName(), e);
        }
        return loaded;
    }

    @Nullable
    private ResourceId getResourceId(Path path) {
        Path sourceRoot = findSourcePath(path);
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
