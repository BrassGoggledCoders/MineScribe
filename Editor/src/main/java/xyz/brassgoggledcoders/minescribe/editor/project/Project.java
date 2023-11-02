package xyz.brassgoggledcoders.minescribe.editor.project;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKey;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Project {
    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);
    public static final InfoKey<Project> KEY = new InfoKey<>() {
    };
    private final Path rootPath;
    private final Path mineScribeFolder;
    private final Map<UUID, Path> openTabs;

    public Project(Path rootPath) {
        this.rootPath = rootPath;
        this.mineScribeFolder = this.rootPath.resolve(".minescribe");
        this.openTabs = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public Path getRootPath() {
        return rootPath;
    }

    public Path getMineScribeFolder() {
        return mineScribeFolder;
    }

    public void addOpenTab(UUID id, Path path) {
        this.openTabs.put(id, path);
    }

    public void removeOpenTab(UUID id) {
        this.openTabs.remove(id);
    }

    public void trySave(Preferences preferences) {
        try {
            Preferences projectNode = preferences.node("last_project");
            projectNode.put("path", this.getRootPath().toString());
            if (!this.openTabs.isEmpty()) {
                Preferences openTabsNode = projectNode.node("open_tabs");
                for (Map.Entry<UUID, Path> entry: this.openTabs.entrySet()) {
                    openTabsNode.put(entry.getKey().toString(), entry.getValue().toString());
                }
                openTabsNode.flush();
            }
            projectNode.flush();
        } catch (BackingStoreException e) {
            LOGGER.error("Failed to save project info", e);
        }
    }

    public Map<UUID, Path> getOpenTabs() {
        return this.openTabs;
    }

    @Nullable
    public static Project tryLoad(Preferences preferences) {
        try {
            if (preferences.nodeExists("last_project")) {
                Preferences projectNode = preferences.node("last_project");
                if (projectNode != null) {
                    String projectPath = projectNode.get("path", "");
                    if (!projectPath.isEmpty()) {
                        Path path = Path.of(projectPath);
                        while (path != null && path.endsWith(".minescribe")) {
                            path = path.getParent();
                        }
                        Project project = new Project(path);

                        if (projectNode.nodeExists("open_tabs")) {
                            Preferences openTabs = projectNode.node("open_tabs");
                            for (String key : openTabs.keys()) {
                                Path tabPath = Path.of(openTabs.get(key, ""));
                                if (Files.isRegularFile(tabPath)) {
                                    project.addOpenTab(UUID.fromString(key), tabPath);
                                }
                            }
                        }
                        return project;
                    }
                }
            }
        } catch (BackingStoreException e) {
            LOGGER.error("Failed to open previously project", e);
        }
        return null;
    }
}
