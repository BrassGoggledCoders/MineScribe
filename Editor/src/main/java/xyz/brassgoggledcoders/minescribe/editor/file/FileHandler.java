package xyz.brassgoggledcoders.minescribe.editor.file;

import javafx.scene.control.TreeItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.PackDirectoryEditorItem;

import java.nio.file.Path;
import java.util.Objects;

public class FileHandler {
    private static FileHandler INSTANCE;

    private final TreeItem<EditorItem> rootItem;

    public FileHandler() {
        this.rootItem = new TreeItem<>();
    }

    public void addPackDirectory(String name, Path path) {
        if (!containsPackDirectory(name, path)) {
            this.rootItem.getChildren()
                    .add(new TreeItem<>(new PackDirectoryEditorItem(name, path)));
        }
    }

    private boolean containsPackDirectory(String name, Path path) {
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
