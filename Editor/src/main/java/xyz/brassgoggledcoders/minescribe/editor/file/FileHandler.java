package xyz.brassgoggledcoders.minescribe.editor.file;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.PackDirectoryEditorItem;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class FileHandler {

    private static FileHandler INSTANCE;

    private final TreeItem<EditorItem> rootItem;

    public FileHandler() {
        this.rootItem = new TreeItem<>();
    }

    public void addPackDirectory(String name, Path path) {
        if (!containsPackDirectory(name, path)) {
            PackDirectoryEditorItem editorItem = new PackDirectoryEditorItem(name, path);
            this.rootItem.getChildren()
                    .add(new TreeItem<>(editorItem));
            this.reloadDirectory(editorItem);
        }
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
        for (EditorItem child : children) {
            TreeItem<EditorItem> childTreeItem = new TreeItem<>(child);
            treeItem.getChildren().add(childTreeItem);
            createChildren(childTreeItem);
        }
    }

    private boolean containsPackDirectory(@Nullable String name, @Nullable Path path) {
        return this.rootItem.getChildren()
                .stream()
                .filter(treeItem -> treeItem.getValue() != null)
                .anyMatch(treeItem -> {
                    EditorItem value = treeItem.getValue();
                    return value.getName().equals(name) || value.getPath().equals(path);
                });
    }

    public TreeItem<EditorItem> getRootModel() {
        return this.rootItem;
    }

    public static void initialize() {
        INSTANCE = new FileHandler();
    }

    public static FileHandler getInstance() {
        return Objects.requireNonNull(INSTANCE, "initialize has not been called");
    }
}
