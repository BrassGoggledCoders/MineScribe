package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.PackContentHierarchy;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.NewFileFormDialog;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.EditorFormTab;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NamespaceEditorItem extends EditorItem {

    private final IPackContentNode contentNode;

    public NamespaceEditorItem(String name, Path path, MineScribePackType packType) {
        super(name, path);
        this.contentNode = PackContentHierarchy.getInstance()
                .getNodeFor(packType);
    }

    @Override
    public @NotNull List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        List<EditorItem> editorItems = new ArrayList<>();
        for (Path childPath : childPaths) {
            if (Files.isDirectory(childPath)) {
                Path relativePath = this.getPath().relativize(childPath);
                IPackContentNode packContentNode = contentNode.getNode(relativePath);

                if (packContentNode != null) {
                    editorItems.add(new PackContentTypeEditorItem(childPath.getFileName().toString(), childPath, packContentNode));
                }
            }
        }
        return editorItems;
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Create Content File");
        menuItem.setOnAction(event -> new NewFileFormDialog(this.contentNode.getNodeTrackers())
                .showAndWait()
                .ifPresent(newFileResult -> {
                    if (newFileResult.getFileForm().isPresent()) {
                        Path path = this.getPath().resolve(newFileResult.parentType().getPath());
                        path = newFileResult.childTypeOpt()
                                .map(PackContentType::getPath)
                                .map(path::resolve)
                                .orElse(path);
                        EditorFormTab editorFormTab = this.getEditorTabService()
                                .openTab("form", path);

                        if (editorFormTab != null) {
                            editorFormTab.fileFormProperty().setValue(newFileResult.getFileForm().get());
                            editorFormTab.parentsProperty()
                                    .setValue(FXCollections.observableList(newFileResult.getFullNames()));
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "No Valid Form Found").show();
                    }
                })
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }
}
