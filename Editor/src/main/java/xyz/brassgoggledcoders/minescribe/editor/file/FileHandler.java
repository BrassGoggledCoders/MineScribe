package xyz.brassgoggledcoders.minescribe.editor.file;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.ExceptionDialog;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.PackRepositoryEditorItem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FileHandler {
    private static final Pattern PATH_NAME_PATTERN = Pattern.compile("\\$\\{PATH:(?<number>-?\\d)}");
    private static FileHandler INSTANCE;
    private static FileWatcher WATCHER;

    private final TreeItem<EditorItem> rootItem;

    private FileHandler() {
        this.rootItem = new TreeItem<>();
    }

    public void reloadClosestNode(@NotNull Path path) {
        this.reloadClosestNode(path, this.rootItem);
    }

    public void reloadClosestNode(@NotNull Path path, TreeItem<EditorItem> treeItem) {
        List<TreeItem<EditorItem>> children = treeItem.getChildren();
        boolean foundNode = false;
        for (TreeItem<EditorItem> child : children) {
            if (path.startsWith(child.getValue().getPath())) {
                reloadClosestNode(path, child);
                foundNode = true;
                break;
            }
        }
        if (!foundNode && treeItem.getValue() != null) {
            reloadDirectory(treeItem.getValue(), treeItem);
        }
    }

    public TreeItem<EditorItem> getClosestNode(@NotNull Path path, boolean expand) {
        Queue<TreeItem<EditorItem>> queue = this.getNodePath(path, this.rootItem);
        TreeItem<EditorItem> closestNode = null;
        while (!queue.isEmpty()) {
            closestNode = queue.poll();
            if (closestNode != null && expand) {
                closestNode.expandedProperty()
                        .set(true);
            }
        }
        return closestNode;
    }

    public Queue<TreeItem<EditorItem>> getNodePath(Path path) {
        return this.getNodePath(path, this.rootItem);
    }

    private Queue<TreeItem<EditorItem>> getNodePath(Path path, TreeItem<EditorItem> treeItem) {
        List<TreeItem<EditorItem>> children = treeItem.getChildren();

        for (TreeItem<EditorItem> child : children) {
            if (path.startsWith(child.getValue().getPath())) {
                Queue<TreeItem<EditorItem>> queue = new LinkedList<>();
                queue.add(child);
                queue.addAll(getNodePath(path, child));

                return queue;
            }
        }

        return new LinkedList<>();
    }

    public void reloadDirectory(@NotNull EditorItem editorItem) {
        this.reloadDirectory(editorItem, this.rootItem);
    }

    public boolean reloadDirectory(@NotNull EditorItem editorItem, TreeItem<EditorItem> treeItem) {
        if (editorItem.equals(treeItem.getValue())) {
            createChildren(treeItem);
            return true;
        } else {
            for (TreeItem<EditorItem> treeItemChild : treeItem.getChildren()) {
                if (this.reloadDirectory(editorItem, treeItemChild)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void createChildren(TreeItem<EditorItem> treeItem) {
        if (treeItem.getValue() != null && treeItem.getValue().isDirectory()) {
            Path currentPath = treeItem.getValue().getPath();
            ObservableList<TreeItem<EditorItem>> currentChildren = treeItem.getChildren();
            List<Path> childrenPaths = currentChildren.stream()
                    .map(TreeItem::getValue)
                    .filter(Objects::nonNull)
                    .map(EditorItem::getPath)
                    .toList();
            currentChildren.removeIf(childItem -> childItem.getValue() == null || !childItem.getValue().isValid());

            try (DirectoryStream<Path> childPaths = Files.newDirectoryStream(currentPath, path -> !childrenPaths.contains(path))) {
                List<EditorItem> children = treeItem.getValue().createChildren(childPaths);
                children.removeIf(Predicate.not(EditorItem::isValid));
                children.sort(EditorItem::compareTo);
                for (EditorItem child : children) {
                    TreeItem<EditorItem> childTreeItem = new TreeItem<>(child);
                    treeItem.getChildren().add(childTreeItem);
                    createChildren(childTreeItem);
                }
            } catch (IOException ioException) {
                ExceptionDialog.showDialog("Failed to generate file list for %s".formatted(currentPath), ioException);
            }
        }


    }

    public TreeItem<EditorItem> getRootModel() {
        return this.rootItem;
    }


    public static void initialize() {
        INSTANCE = new FileHandler();
        try {
            WATCHER = FileWatcher.of(
                    INSTANCE::handleUpdates,
                    throwable -> ExceptionDialog.showDialog("File Watcher Exception", throwable)
            );
        } catch (IOException e) {
            ExceptionDialog.showDialog("Failed to initialize FileWatcher", e);
            Platform.exit();
        }
        Project project = InfoRepository.getInstance()
                .getValue(Project.KEY);
        if (project != null) {
            for (PackRepositoryLocation location : Registries.getPackRepositoryLocations()) {
                PathMatcher pathMatcher = project.getRootPath()
                        .getFileSystem()
                        .getPathMatcher("glob:" + location.pathMatcher());
                List<Path> packRepositoryPaths = findPackRepositories(project.getRootPath(), pathMatcher, 1);

                for (Path packRepositoryPath : packRepositoryPaths) {
                    String repositoryLabel = location.label();

                    repositoryLabel = PATH_NAME_PATTERN.matcher(repositoryLabel)
                            .replaceAll(matchResult -> {
                                int pathPosition = Integer.parseInt(matchResult.group(1));
                                if (Math.abs(pathPosition) < packRepositoryPath.getNameCount()) {
                                    if (pathPosition >= 0) {
                                        return packRepositoryPath.getName(pathPosition)
                                                .toString();
                                    } else {
                                        return packRepositoryPath.getName(packRepositoryPath.getNameCount() - Math.abs(pathPosition))
                                                .toString();
                                    }
                                }

                                return "";
                            });

                    PackRepositoryEditorItem editorItem = new PackRepositoryEditorItem(repositoryLabel, packRepositoryPath);
                    INSTANCE.rootItem.getChildren()
                            .add(new TreeItem<>(editorItem));
                    WATCHER.watchDirectory(editorItem.getPath());
                    INSTANCE.reloadDirectory(editorItem);
                }
            }
        }
    }

    @NotNull
    private static List<Path> findPackRepositories(Path folder, PathMatcher pathMatcher, int depth) {
        List<Path> matchedPaths = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
            for (Path path : directoryStream) {
                if (pathMatcher.matches(path)) {
                    matchedPaths.add(path);
                } else if (depth < 3 && Files.isDirectory(path)) {
                    matchedPaths.addAll(findPackRepositories(path, pathMatcher, depth + 1));
                }
            }
        } catch (IOException ioException) {
            ExceptionDialog.showDialog("Failed to find pack repository", ioException);

        }
        return matchedPaths;
    }

    private void handleUpdates(FileUpdate fileUpdate) {
        this.reloadClosestNode(fileUpdate.path());
    }

    public static void dispose() {
        try {
            WATCHER.close();
        } catch (Exception e) {
            ExceptionDialog.showDialog("Failed to clean up FileHandler", e);
        }
    }

    public static FileHandler getInstance() {
        return Objects.requireNonNull(INSTANCE, "initialize has not been called");
    }
}
