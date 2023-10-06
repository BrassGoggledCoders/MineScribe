package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.NewPackController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.TabEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PackDirectoryEditorItem extends EditorItem {
    public PackDirectoryEditorItem(String name, Path path) {
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
        childrenEditorItems.removeIf(Predicate.not(EditorItem::isValid));

        return childrenEditorItems;
    }
}
