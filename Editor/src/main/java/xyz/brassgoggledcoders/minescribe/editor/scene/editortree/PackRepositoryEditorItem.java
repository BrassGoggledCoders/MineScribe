package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.controlsfx.dialog.ExceptionDialog;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.MultiSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.StringFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.RegistryFormList;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.EditorFormDialog;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.PackCreationResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackRepositoryEditorItem extends EditorItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackRepositoryEditorItem.class);

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public PackRepositoryEditorItem(String label, Path path) {
        super(label, path);
    }

    @Override
    @NotNull
    public ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem createNewPack = new MenuItem("Create New Pack");
        createNewPack.setOnAction(event -> {
            EditorFormDialog<PackCreationResult> editorFormDialog = EditorFormDialog.of(
                    PackCreationResult.CODEC,
                    FileForm.of(
                            new FileField<>(
                                    new MultiSelectionFileFieldDefinition(Collections.singletonList(
                                            new RegistryFormList<>(Registries.getPackTypeRegistry())
                                    )),
                                    new FileFieldInfo(
                                            "Pack Type",
                                            "packTypes",
                                            0,
                                            true
                                    )
                            ),
                            new FileField<>(
                                    new StringFileFieldDefinition(""),
                                    new FileFieldInfo(
                                            "Name",
                                            "name",
                                            1,
                                            true
                                    )
                            ),
                            new FileField<>(
                                    new StringFileFieldDefinition(""),
                                    new FileFieldInfo(
                                            "Description",
                                            "description",
                                            2,
                                            false
                                    )
                            )
                    )
            );
            editorFormDialog.setTitle("Create New Pack");
            editorFormDialog.showAndWait()
                    .ifPresent(result -> handleCreate(treeCell.getItem(), result));
        });
        contextMenu.getItems().add(0, createNewPack);
        return contextMenu;
    }

    private void handleCreate(EditorItem parentItem, PackCreationResult result) {
        Path packPath = parentItem.getPath().resolve(Path.of(result.name()));
        if (!Files.exists(packPath)) {
            try {
                Files.createDirectories(packPath);
                boolean createdAllPackTypeFolder = true;
                for (MineScribePackType packType : result.packTypes()) {
                    createdAllPackTypeFolder &= packPath.resolve(packType.folder()).toFile().mkdir();
                }
                if (createdAllPackTypeFolder) {
                    int packVersion = result.packTypes()
                            .stream()
                            .mapToInt(MineScribePackType::version)
                            .min()
                            .orElse(0);

                    JsonObject packMeta = new JsonObject();
                    JsonObject packObject = new JsonObject();
                    packMeta.add("pack", packObject);

                    packObject.addProperty("pack_format", packVersion);
                    packObject.addProperty("description", result.description().orElse(""));
                    for (MineScribePackType packType : result.packTypes()) {
                        packType.versionKey()
                                .ifPresent(versionKey -> packObject.addProperty(versionKey, packType.version()));

                    }

                    try {
                        Files.write(
                                parentItem.getPath().resolve(result.name() + "/pack.mcmeta"),
                                GSON.toJson(packMeta).getBytes()
                        );
                        this.getEditorItemService()
                                .reloadDirectory(parentItem);
                    } catch (IOException e) {
                        LOGGER.error("Failed to write pack.mcmeta", e);
                    }
                } else {
                    LOGGER.error("Failed to create all pack type folder");
                }
            } catch (IOException ioException) {
                LOGGER.error("Failed to create directories", ioException);
                new ExceptionDialog(ioException)
                        .showAndWait();
            }
        }
    }

    @Override
    @NotNull
    public List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        List<EditorItem> childrenEditorItems = new ArrayList<>();
        for (Path childFolder : childPaths) {
            childrenEditorItems.add(new PackEditorItem(childFolder.getFileName().toString(), childFolder));
        }

        return childrenEditorItems;
    }
}
