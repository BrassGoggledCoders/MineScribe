package xyz.brassgoggledcoders.minescribe.editor.file;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.PackRepositoryEditorItem;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

public class FileHandler {

    private static FileHandler INSTANCE;

    private final TreeItem<EditorItem> rootItem;

    public FileHandler() {
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
        treeItem.getChildren().clear();
        List<EditorItem> children = treeItem.getValue().createChildren();
        children.removeIf(Predicate.not(EditorItem::isValid));
        children.sort(EditorItem::compareTo);
        for (EditorItem child : children) {
            TreeItem<EditorItem> childTreeItem = new TreeItem<>(child);
            treeItem.getChildren().add(childTreeItem);
            createChildren(childTreeItem);
        }
    }

    public TreeItem<EditorItem> getRootModel() {
        return this.rootItem;
    }


    public static void initialize() {
        INSTANCE = new FileHandler();
        for (PackRepositoryLocation location : Registries.getPackRepositoryLocations()) {
            PackRepositoryEditorItem editorItem = new PackRepositoryEditorItem(location);
            INSTANCE.rootItem.getChildren()
                    .add(new TreeItem<>(editorItem));
            INSTANCE.reloadDirectory(editorItem);
        }
    }

    public static FileHandler getInstance() {
        return Objects.requireNonNull(INSTANCE, "initialize has not been called");
    }
}
