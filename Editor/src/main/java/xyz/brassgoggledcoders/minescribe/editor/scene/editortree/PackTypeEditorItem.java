package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.StringFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.EditorFormDialog;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.NewNamespaceResult;
import xyz.brassgoggledcoders.minescribe.editor.validation.RegexFieldValidation;

import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PackTypeEditorItem extends EditorItem {
    private final MineScribePackType packType;

    public PackTypeEditorItem(String name, Path path, MineScribePackType packType) {
        super(name, path);
        this.packType = packType;
        if (packType.name().equalsIgnoreCase("MINESCRIBE")) {
            EditorRegistries.addSourcePath(path);
        }
    }

    @Override
    public @NotNull List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        return StreamSupport.stream(childPaths.spliterator(), false)
                .<EditorItem>map(path -> new NamespaceEditorItem(path.getFileName().toString(), path, this.packType))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid() {
        return this.packType != null;
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Create New Namespace");
        menuItem.setOnAction(event -> {
                    EditorFormDialog<NewNamespaceResult> editorFormDialog = EditorFormDialog.of(
                            NewNamespaceResult.CODEC,
                            FileForm.of(
                                    new FileField<>(
                                            new StringFileFieldDefinition(""),
                                            new FileFieldInfo(
                                                    "Namespace",
                                                    "namespace",
                                                    0,
                                                    true,
                                                    Collections.singletonList(new RegexFieldValidation(
                                                            "^[a-z0-9\\.\\-_]+$",
                                                            "%s can only contain alphanumeric characters, ., -, or _"
                                                    ))
                                            )
                                    )
                            )
                    );
                    editorFormDialog.setTitle("New Namespace");
                    editorFormDialog.showAndWait()
                            .map(NewNamespaceResult::namespace)
                            .ifPresent(namespace -> {
                                boolean createdFolder = this.getPath()
                                        .resolve(namespace)
                                        .toFile()
                                        .mkdirs();

                                if (createdFolder) {
                                    this.getEditorItemService()
                                            .reloadDirectory(this);
                                }
                            });
                }
        );
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }
}
