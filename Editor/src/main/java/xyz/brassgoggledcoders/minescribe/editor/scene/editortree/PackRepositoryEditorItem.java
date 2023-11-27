package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.NewPackController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;

import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackRepositoryEditorItem extends EditorItem {
    public PackRepositoryEditorItem(String label, Path path) {
        super(label, path);
    }

    @Override
    @NotNull
    public ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem createNewPack = new MenuItem("Create New Pack");
        createNewPack.setOnAction(event -> {
            //new FormDialog<String>().showAndWait());

            treeCell.fireEvent(new OpenTabEvent<NewPackController>("Create New Pack", "tab/new_pack", (controller, tabId) -> {
                controller.setParentItem(treeCell.getItem());
                controller.setTabId(tabId);
            }));
        });
        contextMenu.getItems().add(0, createNewPack);
        return contextMenu;
    }

    @Override
    @NotNull
    public List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        List<EditorItem> childrenEditorItems = new ArrayList<>();
        for (Path childFolder : childPaths) {
            childrenEditorItems.add(new PackEditorItem(childFolder.getFileName().toString(), childFolder));
        }

        return childrenEditorItems;
    }
}
