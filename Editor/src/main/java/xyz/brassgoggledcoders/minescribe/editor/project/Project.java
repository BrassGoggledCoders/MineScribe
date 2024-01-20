package xyz.brassgoggledcoders.minescribe.editor.project;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Project {
    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);
    private final Path rootPath;
    private final Path mineScribeFolder;
    private final Map<String, Path> additionalPackLocations;
    private final Set<Path> openTabs;

    public Project(Path rootPath) {
        this.rootPath = rootPath;
        this.mineScribeFolder = this.rootPath.resolve(".minescribe");
        this.openTabs = new HashSet<>();
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

    public void addOpenTab(Path path) {
        this.openTabs.add(path);
    }

    public void removeOpenTab(Path path) {
        this.openTabs.remove(path);
    }

    public void trySave(Preferences preferences) {
        try {
            Preferences projectNode = preferences.node("last_project");
            projectNode.put("path", this.getRootPath().toString());
            if (!this.openTabs.isEmpty()) {
                projectNode.put("tabs", this.openTabs.stream()
                        .map(Path::toString)
                        .collect(Collectors.joining(","))
                );
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

    public Set<Path> getOpenTabs() {
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

                        String tabs = projectNode.get("tabs", "");
                        if (!tabs.isEmpty()) {
                            Arrays.stream(tabs.split(","))
                                    .map(Path::of)
                                    .forEach(project::addOpenTab);
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
