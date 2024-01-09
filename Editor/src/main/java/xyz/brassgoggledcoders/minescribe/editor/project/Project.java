package xyz.brassgoggledcoders.minescribe.editor.project;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Project {
    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);
    private final Path rootPath;
    private final Path mineScribeFolder;
    private final Map<String, Path> additionalPackLocations;
    private final Map<UUID, Path> openTabs;

    public Project(Path rootPath) {
        this.rootPath = rootPath;
        this.mineScribeFolder = this.rootPath.resolve(".minescribe");
        this.openTabs = new HashMap<>();
        this.additionalPackLocations = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public Path getRootPath() {
        return rootPath;
    }

    public Path getMineScribeFolder() {
        return mineScribeFolder;
    }

    public Map<String, Path> getAdditionalPackLocations() {
        return additionalPackLocations;
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
            Preferences openTabsNode = projectNode.node("open_tabs");
            openTabsNode.clear();
            if (!this.openTabs.isEmpty()) {
                for (Map.Entry<UUID, Path> entry : this.openTabs.entrySet()) {
                    openTabsNode.put(entry.getKey().toString(), entry.getValue().toString());
                }
                openTabsNode.flush();
            }
            projectNode.put(
                    "additional_pack_locations",
                    this.additionalPackLocations.entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                            .collect(Collectors.joining(","))
            );

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

                        project.getAdditionalPackLocations()
                                .putAll(Arrays.stream(projectNode.get("additional_pack_locations", "").split(","))
                                        .flatMap(fileName -> {
                                            try {
                                                String[] split = fileName.split("=");
                                                if (split.length == 2) {
                                                    return Stream.of(Pair.of(split[0], Path.of(split[1])));
                                                }
                                            } catch (InvalidPathException e) {
                                                LOGGER.warn("Failed to create additional pack location from {}", fileName, e);
                                            }
                                            return Stream.empty();
                                        })
                                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
                                );
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
