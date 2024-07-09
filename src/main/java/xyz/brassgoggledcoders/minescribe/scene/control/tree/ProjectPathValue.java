package xyz.brassgoggledcoders.minescribe.scene.control.tree;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectPathValue implements Comparable<ProjectPathValue> {
    private final String name;
    private final Path path;

    private final boolean directory;

    public ProjectPathValue(String name, Path path) {
        this.name = name;
        this.path = path;

        this.directory = Files.isDirectory(path);
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirectory() {
        return directory;
    }

    @Override
    public int compareTo(@NotNull ProjectPathValue o) {
        if (this.isDirectory() && !o.isDirectory()) {
            return -1;
        } else if (!this.isDirectory() && o.isDirectory()) {
            return 1;
        } else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), o.getName());
        }
    }
}
