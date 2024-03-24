package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.NewFileFormDialog;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.EditorFormTab;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PackContentTypeEditorItem extends EditorItem {
    private final IPackContentNode contentNode;

    public PackContentTypeEditorItem(String name, Path path, IPackContentNode contentNode) {
        super(name, path);
        this.contentNode = contentNode;
    }

    @Override
    public @NotNull List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        List<EditorItem> editorItems = new ArrayList<>();
        for (Path childPath : childPaths) {
            Path relativePath = this.getPath().relativize(childPath);
            if (Files.isDirectory(childPath)) {
                IPackContentNode packContentNode = contentNode.getNode(relativePath);

                if (packContentNode != null) {
                    editorItems.add(new PackContentTypeEditorItem(
                            relativePath.toString(),
                            childPath,
                            packContentNode
                    ));
                }
            } else if (Files.isRegularFile(childPath)) {
                editorItems.add(new FormFileEditorItem(
                        relativePath.toString(),
                        childPath,
                        contentNode.getNodeTrackers()
                ));
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

                    Optional<NamespaceEditorItem> namespaceEditorItem = this.getEditorItemService()
                            .getNodePath(this.getPath())
                            .stream()
                            .map(TreeItem::getValue)
                            .filter(NamespaceEditorItem.class::isInstance)
                            .map(NamespaceEditorItem.class::cast)
                            .findFirst();

                    Optional<FileForm> fileForm = newFileResult.getFileForm();
                    if (fileForm.isPresent() && namespaceEditorItem.isPresent()) {
                        Path filePath = namespaceEditorItem.get()
                                .getPath()
                                .resolve(newFileResult.parentType()
                                        .getPath()
                                );

                        filePath = newFileResult.childTypeOpt()
                                .map(PackContentType::getPath)
                                .map(filePath::resolve)
                                .orElse(filePath);

                        if (filePath.compareTo(this.getPath()) < 0) {
                            filePath = this.getPath();
                        }

                        EditorFormTab editorFormTab = this.getEditorTabService()
                                .openTab("form", filePath);

                        if (editorFormTab != null) {
                            editorFormTab.fileFormProperty()
                                    .setValue(fileForm.get());
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
