package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.PackContentHierarchy;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.NewFileFormDialog;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NamespaceEditorItem extends EditorItem {
    private final IPackContentNode contentNode;

    public NamespaceEditorItem(String name, Path path, MineScribePackType packType) {
        super(name, path);
        this.contentNode = PackContentHierarchy.getInstance()
                .getNodeFor(packType);
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        List<File> childrenFiles = this.getChildrenFiles();
        List<EditorItem> editorItems = new ArrayList<>();
        for (File childFile : childrenFiles) {
            if (childFile.isDirectory()) {
                Path childPath = this.getPath().relativize(childFile.toPath());
                IPackContentNode packContentNode = contentNode.getNode(childPath);

                if (packContentNode != null) {
                    editorItems.add(new PackContentTypeEditorItem(childFile.getName(), childFile.toPath(), packContentNode));
                }
            }
        }
        return editorItems;
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Create Content File");
        menuItem.setOnAction(event -> new NewFileFormDialog(Collections.emptyList())
                .showAndWait()
                .ifPresent(System.out::println)
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }
}
