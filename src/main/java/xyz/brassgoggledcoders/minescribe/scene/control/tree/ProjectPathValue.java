package xyz.brassgoggledcoders.minescribe.scene.control.tree;

import java.nio.file.Path;

public class ProjectPathValue {
    private final String name;
    private final Path path;

    public ProjectPathValue(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }
}
