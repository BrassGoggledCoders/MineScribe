package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class EditorItem {
    private final String name;
    private final Path path;

    public EditorItem(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    @Nullable
    public ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        return null;
    }

}
