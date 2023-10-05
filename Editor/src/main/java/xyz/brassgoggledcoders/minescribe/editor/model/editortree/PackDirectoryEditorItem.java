package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.NewPackController;
import xyz.brassgoggledcoders.minescribe.editor.event.TabEvent;

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
            //new FormDialog<String>().showAndWait());

            treeCell.fireEvent(new TabEvent.OpenTabEvent<NewPackController>("Create New Pack", "tab/new_pack", controller -> {
                controller.setParentItem(treeCell.getItem());
            }));
        });
        contextMenu.getItems().add(createNewPack);
        return contextMenu;
    }
}
