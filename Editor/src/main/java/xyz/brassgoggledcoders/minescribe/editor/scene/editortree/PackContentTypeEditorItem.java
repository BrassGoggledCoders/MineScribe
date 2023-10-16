package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.NewFileFormDialog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class PackContentTypeEditorItem extends EditorItem {
    private final IPackContentNode contentNode;

    public PackContentTypeEditorItem(String name, Path path, IPackContentNode contentNode) {
        super(name, path);
        this.contentNode = contentNode;
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return this.runForChildren(childPath -> {
            Path relativePath = this.getPath().relativize(childPath);
            if (Files.isDirectory(childPath)) {
                IPackContentNode packContentNode = contentNode.getNode(relativePath);

                if (packContentNode != null) {
                    return Optional.of(new PackContentTypeEditorItem(
                            relativePath.toString(),
                            childPath,
                            packContentNode
                    ));
                }
            } else if (Files.isRegularFile(childPath)) {
                return Optional.of(new FileEditorItem(
                        relativePath.toString(),
                        childPath
                ));
            }

            return Optional.empty();
        });
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Create Content File");
        menuItem.setOnAction(event -> new NewFileFormDialog(this.contentNode.getNodeTrackers())
                .showAndWait()
                .ifPresent(System.out::println)
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }
}
