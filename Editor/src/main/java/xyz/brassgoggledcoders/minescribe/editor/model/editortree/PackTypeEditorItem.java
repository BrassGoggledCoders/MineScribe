package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackTypeInfo;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.model.dialog.NewDirectoryFormDialog;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class PackTypeEditorItem extends EditorItem {
    private final PackTypeInfo packTypeInfo;

    public PackTypeEditorItem(String name, Path path, PackTypeInfo packTypeInfo) {
        super(name, path);
        this.packTypeInfo = packTypeInfo;
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return this.getChildrenFiles(File::isDirectory)
                .stream()
                .<EditorItem>map(file -> new NamespaceEditorItem(file.getName(), file.toPath(), this.packTypeInfo))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid() {
        return this.packTypeInfo != null;
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
