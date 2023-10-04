package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.editor.model.FormDialog;

import java.nio.file.Path;

public class PackDirectoryEditorItem extends EditorItem {
    public PackDirectoryEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public @Nullable ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem createNewPack = new MenuItem("Create New Pack");
        contextMenu.setOnAction(event -> {
            new FormDialog<String>().showAndWait();
        });
        contextMenu.getItems().add(createNewPack);
        return contextMenu;
    }
}
