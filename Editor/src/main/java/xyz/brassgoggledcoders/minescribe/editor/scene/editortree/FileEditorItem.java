package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.NoFormController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileEditorItem extends EditorItem {
    public FileEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public boolean isValid() {
        File file = this.getFile();
        return file.isFile();
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return Collections.emptyList();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Open File");
        menuItem.setOnAction(event -> treeCell.fireEvent(
                new OpenTabEvent<NoFormController>(
                        treeCell.getItem().getName(),
                        "tab/no_form",
                        (controller, tabId) -> controller.setPathToFile(treeCell.getItem().getPath()))
        ));
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }
}