package xyz.brassgoggledcoders.minescribe.service.file;

import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.collection.namedtree.NamedTree;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DirectoryWatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryWatchService.class);

    private final ProjectService projectService;

    private final ObservableMap<Path, NamedTree<String, Path>> watchedPaths;

    private DirectoryWatcher directoryWatcher;

    @Autowired
    public DirectoryWatchService(ProjectService projectService) {
        this.projectService = projectService;

        this.watchedPaths = FXCollections.observableHashMap();

        this.projectService.projectProperty()
                .flatMap(Project::importedPacksProperty)
                .subscribe(this::handleWatchChange);
    }

    public ObservableMap<Path, NamedTree<String, Path>> getWatchedPaths() {
        return watchedPaths;
    }

    public NamedTree<String, Path> getWatchedPath(Path path) {
        return this.getWatchedPaths()
                .get(path);
    }

    private void handleDirectoryChange(DirectoryChangeEvent directoryChangeEvent) {
        NamedTree<String, Path> tree = watchedPaths.get(directoryChangeEvent.rootPath());
        Path changedPath = directoryChangeEvent.path();
        Path relativePath = directoryChangeEvent.rootPath()
                .relativize(changedPath);

        switch (directoryChangeEvent.eventType()) {
            case CREATE -> this.handleCreate(tree, relativePath);
            case DELETE -> this.handleDelete(tree, relativePath);
        }
    }

    private void handleDelete(NamedTree<String, Path> tree, Path relativePath) {
        NamedTree<String, Path> child = tree;
        Iterator<Path> paths = relativePath.iterator();
        while(paths.hasNext() && child != null) {
            child = child.getChild(paths.next().toString());
        }
        if (child != null) {
            child.getParent().remove(child);
        }
    }

    private void handleCreate(NamedTree<String, Path> tree, Path relativePath) {
        NamedTree<String, Path> child = tree;
        for (Path folderPath : relativePath) {
            NamedTree<String, Path> current = child;
            child = current.getChild(folderPath.toString());
            if (child == null) {
                child = current.addChild(folderPath.toString(), folderPath.toAbsolutePath());
            }
        }
    }

    private void handleWatchChange(
            @Nullable ObservableList<String> oldValue,
            @Nullable ObservableList<String> newValue
    ) {
        List<String> toRemove = new ArrayList<>();
        List<String> toAdd = new ArrayList<>();
        if (newValue != null) {
            toAdd.addAll(newValue);
        }
        if (oldValue != null) {
            toAdd.removeAll(oldValue);
            if (newValue != null) {
                toRemove.addAll(oldValue);
                toAdd.removeAll(newValue);
            }
        }

        for (String stringPath : toRemove) {
            Path path = Path.of(stringPath);
            if (!path.isAbsolute()) {
                path = this.projectService.projectPathProperty()
                        .getValue()
                        .resolve(path);
            }
            this.watchedPaths.remove(path);
        }

        for (String stringPath : toRemove) {
            this.watchedPaths.remove(this.projectService.makeProjectPath(stringPath));
        }

        for (String stringPath : toAdd) {
            Path path = this.projectService.makeProjectPath(stringPath);
            this.watchedPaths.put(
                    this.projectService.makeProjectPath(stringPath),
                    this.buildNamedTree(path)
            );
        }

        if (!toAdd.isEmpty() || !toRemove.isEmpty()) {
            this.rebuildDirectoryWatcher();
        }
    }

    private NamedTree<String, Path> buildNamedTree(Path root) {
        NamedTree<String, Path> treeRoot = new NamedTree<>(null, root);
        gatherChildren(treeRoot);
        return treeRoot;
    }

    private void gatherChildren(NamedTree<String, Path> namedTree) {
        Path parentPath = namedTree.getValue();
        try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(parentPath, Files::exists)) {
            for (Path path : pathDirectoryStream) {
                String name = path.getName(parentPath.getNameCount())
                        .toString();
                NamedTree<String, Path> childNamedTree = namedTree.addChild(name, path);
                if (Files.isDirectory(path)) {
                    gatherChildren(childNamedTree);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to gather children for {}", parentPath, e);
        }
    }

    private void rebuildDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close directoryWatcher", e);
            }
            this.directoryWatcher = null;
        }

        try {
            this.directoryWatcher = DirectoryWatcher.builder()
                    .paths(new ArrayList<>(this.watchedPaths.keySet()))
                    .listener(this::handleDirectoryChange)
                    .logger(LOGGER)
                    .build();
            this.directoryWatcher.watchAsync();
        } catch (IOException e) {
            LOGGER.error("Failed to create directoryWatcher", e);
        }
    }
}
