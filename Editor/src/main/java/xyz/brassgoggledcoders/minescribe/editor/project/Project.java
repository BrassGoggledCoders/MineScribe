package xyz.brassgoggledcoders.minescribe.editor.project;

import xyz.brassgoggledcoders.minescribe.core.info.InfoKey;

import java.nio.file.Path;

public class Project {
    public static final InfoKey<Project> KEY = new InfoKey<>() {
    };
    private final Path rootPath;
    private final Path mineScribeFolder;

    public Project(Path rootPath) {
        this.rootPath = rootPath;
        this.mineScribeFolder = this.rootPath.resolve(".minescribe");
    }

    @SuppressWarnings("unused")
    public Path getRootPath() {
        return rootPath;
    }

    public Path getMineScribeFolder() {
        return mineScribeFolder;
    }
}
