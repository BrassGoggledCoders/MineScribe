package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.PackContentHierarchy;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.FormController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.NewFileFormDialog;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        menuItem.setOnAction(event -> new NewFileFormDialog(this.contentNode.getNodeTrackers())
                .showAndWait()
                .ifPresent(newFileResult -> {
                    Optional<FileForm> fileForm = newFileResult.parentType().getForm()
                            .or(() -> newFileResult.childTypeOpt()
                                    .flatMap(PackContentType::getForm)
                            );

                    if (fileForm.isPresent()) {
                        Path path = this.getPath().resolve(newFileResult.parentType().getPath());
                        path = newFileResult.childTypeOpt()
                                .map(PackContentType::getPath)
                                .map(path::resolve)
                                .orElse(path);
                        Path finalPath = path;
                        treeCell.fireEvent(new OpenTabEvent<FormController>(
                                newFileResult.fileName(),
                                "tab/form",
                                (controller, tabId) -> controller.setFormInfo(
                                        finalPath.resolve(newFileResult.fileName()),
                                        newFileResult.parentType(),
                                        newFileResult.childTypeOpt()
                                                .orElse(null)
                                )
                        ));
                    } else {
                        new Alert(Alert.AlertType.ERROR, "No Valid Form Found").show();
                    }
                })
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }
}
