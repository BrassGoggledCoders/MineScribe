package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.model.dialog.NewDirectoryFormDialog;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class PackTypeEditorItem extends EditorItem {
    public PackTypeEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return this.getChildrenFiles(File::isDirectory)
                .stream()
                .<EditorItem>map(file -> new NamespaceEditorItem(file.getName(), file.toPath()))
                .toList();
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {

        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Create Namespace Folder");
        menuItem.setOnAction(event -> new NewDirectoryFormDialog()
                .showAndWait()
                .ifPresent(folderName -> {
                    boolean createdFolder = this.getPath()
                            .resolve(folderName)
                            .toFile()
                            .mkdirs();

                    if (createdFolder) {
                        FileHandler.getInstance()
                                .reloadDirectory(this);
                    }
                })
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }
}
