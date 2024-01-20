package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public class FileViewFileEditorItem extends FileEditorItem {
    public FileViewFileEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public boolean isValid() {
        File file = this.getFile();
        return file.isFile();
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Open File");
        menuItem.setOnAction(event -> this.getEditorTabService()
                .openTab("file_view", this.getPath())
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }

    @Override
    public void onDoubleClick(TreeCell<EditorItem> treeCell) {
        this.getEditorTabService()
                .openTab("file_view", this.getPath());
    }
}
