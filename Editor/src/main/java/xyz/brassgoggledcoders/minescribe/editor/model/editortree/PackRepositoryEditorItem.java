package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.NewPackController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackRepositoryEditorItem extends EditorItem {
    public PackRepositoryEditorItem(String name, Path path) {
        super(name, path);
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
    public List<EditorItem> createChildren() {
        File[] childrenFolders = this.getPath()
                .toFile()
                .listFiles(File::isDirectory);

        List<EditorItem> childrenEditorItems = new ArrayList<>();
        if (childrenFolders != null) {
            for (File childFolder : childrenFolders) {
                childrenEditorItems.add(new PackEditorItem(childFolder.getName(), childFolder.toPath()));
            }
        }

        return childrenEditorItems;
    }
}
