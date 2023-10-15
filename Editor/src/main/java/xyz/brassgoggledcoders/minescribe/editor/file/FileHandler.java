package xyz.brassgoggledcoders.minescribe.editor.file;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.PackRepositoryEditorItem;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FileHandler {

    private static FileHandler INSTANCE;

    private final TreeItem<EditorItem> rootItem;

    public FileHandler() {
        this.rootItem = new TreeItem<>();
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
