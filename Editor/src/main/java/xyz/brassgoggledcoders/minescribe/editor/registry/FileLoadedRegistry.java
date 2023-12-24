package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.editor.file.FileUpdate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class FileLoadedRegistry<K, V> extends Registry<K, V> implements IFileUpdateListener, ISourceRootListener {
    private final Logger logger;
    private final Map<Path, PathMatcher> sourcePaths;
    private final String directory;
    private final String fileType;

    public FileLoadedRegistry(String name, Codec<K> kCodec, String directory, String fileType) {
        super(name, kCodec);
        this.logger = LoggerFactory.getLogger(name + " registry");
        this.sourcePaths = new HashMap<>();
        this.directory = directory.replace("/", File.separator)
                .replace("\\", File.separator);
        this.fileType = fileType;
    }

    protected abstract int handleFileInFolder(Path path, ResourceId id, String fileContents);

    private PathMatcher createPathMatcher(Path path) {
        String stringMatcher = "**" + File.separator +
                this.directory + File.separator + "**." + this.fileType;
        stringMatcher = stringMatcher.replace("\\", "\\\\");

        return path.getFileSystem()
                .getPathMatcher("glob:" + stringMatcher);
    }

    public void load(Path sourcePath) {
        this.loadFolder(sourcePath, true);
    }

    private void loadFolder(Path sourcePath, boolean original) {
        if (!this.sourcePaths.containsKey(sourcePath)) {
            PathMatcher matcher = createPathMatcher(sourcePath);
            this.sourcePaths.put(sourcePath, matcher);

            int loaded = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        loaded += readFolder(path, matcher);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to load Values for registry {}", this.getId(), e);
            }
            if (original || loaded > 0) {
                logger.info("Loaded {} values for registry {} from {}", loaded, this.getId(), sourcePath);
            }
        }
    }

    @Override
    public void addSourceRoot(Path sourceRoot) {
        this.loadFolder(sourceRoot, false);
    }

    private int readFolder(Path parent, PathMatcher matcher) {
        int loaded = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, Files::exists)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    loaded += readFolder(path, matcher);
                } else if (matcher.matches(path)) {
                    loaded += loadFile(path);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load Values for registry {}", this.getId(), e);
        }
        return loaded;
    }

    @Nullable
    private ResourceId getResourceId(Path path) {
        Entry<Path, PathMatcher> sourceRoot = findSourcePath(path);
        ResourceId id = null;
        if (sourceRoot != null) {
            String relativePath = sourceRoot.getKey()
                    .relativize(path)
                    .toString()
                    .replace("." + this.fileType, "");
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
    protected Entry<Path, PathMatcher> findSourcePath(@NotNull Path path) {
        return this.sourcePaths.entrySet()
                .stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .findFirst()
                .orElse(null);
    }

    private int loadFile(Path path) {
        try {
            String jsonString = Files.readString(path, StandardCharsets.UTF_8);
            ResourceId id = getResourceId(path);
            if (id != null) {
                return handleFileInFolder(path, id, jsonString);
            } else {
                logger.error("Failed to convert {} to an id", path);
            }
        } catch (IOException e) {
            logger.error("Failed to load Value for file {}", path, e);
        }
        return 0;
    }

    @Override
    public void fileUpdated(FileUpdate fileUpdate) {
        Path path = fileUpdate.path();
        Entry<Path, PathMatcher> sourcePath = findSourcePath(path);
        if (sourcePath != null) {
            if (sourcePath.getValue().matches(path)) {
                loadFile(path);
            }
        }
    }
}
