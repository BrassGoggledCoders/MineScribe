package xyz.brassgoggledcoders.minescribe.editor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.controller.element.InfoPaneController;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorTreeCell;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.FormFileEditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.IFileTab;
import xyz.brassgoggledcoders.minescribe.editor.service.editoritem.IEditorItemService;
import xyz.brassgoggledcoders.minescribe.editor.service.tab.IEditorTabService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditorController {
    private final Logger LOGGER = LoggerFactory.getLogger(EditorController.class);
    private final Gson GSON = new GsonBuilder()
            .create();


    private final IEditorItemService editorItemService;
    private final IEditorTabService editorTabService;
    private final Provider<Project> projectProvider;

    @FXML
    public SplitPane editor;
    @FXML
    public TreeView<EditorItem> files;
    @FXML
    public TabPane editorTabPane;
    @FXML
    private SplitPane parentPane;
    @FXML
    @SuppressWarnings("unused")
    private Parent infoPane;
    @FXML
    @SuppressWarnings("unused")
    private InfoPaneController infoPaneController;

    @Inject
    public EditorController(IEditorItemService editorItemService, IEditorTabService editorTabService, Provider<Project> projectProvider) {
        this.editorItemService = editorItemService;
        this.editorTabService = editorTabService;
        this.projectProvider = projectProvider;
    }

    @FXML
    public void initialize() {
        files.setRoot(this.editorItemService.getRootItem());
        files.setCellFactory(param -> new EditorTreeCell());

        this.infoPaneController.setParentPane(this.parentPane);
        this.editorTabService.setEditorTabPane(this.editorTabPane);

        Project project = projectProvider.get();
        if (project != null) {
            for (Path openTab : project.getOpenTabs()) {
                TreeItem<EditorItem> treeItem = this.editorItemService.getClosestNode(openTab, true);
                if (treeItem != null && treeItem.getValue() instanceof FormFileEditorItem fileEditorItem) {
                    fileEditorItem.openTab();
                }
            }
        }
        this.editorTabPane.getTabs()
                .addListener((ListChangeListener<Tab>) c -> {
                    Project currentProject = this.projectProvider.get();
                    if (currentProject != null) {
                        while (c.next()) {
                            if (c.wasRemoved()) {
                                for (Tab removedTab : c.getRemoved()) {
                                    if (removedTab instanceof IFileTab fileTab) {
                                        currentProject.removeOpenTab(fileTab.pathProperty().get());
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @FXML
    private void addPack(ActionEvent ignored) {
        Project project = this.projectProvider.get();
        if (project != null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(project.getRootPath()
                    .toFile()
            );
            File newDirectory = directoryChooser.showDialog(this.editor.getScene()
                    .getWindow()
            );

            if (newDirectory != null && newDirectory.isDirectory()) {
                File mcMeta = new File(newDirectory, "pack.mcmeta");
                if (mcMeta.exists() && mcMeta.isFile()) {
                    try {
                        String packDescription = null;
                        JsonElement jsonElement = GSON.fromJson(Files.readString(mcMeta.toPath()), JsonElement.class);
                        if (jsonElement != null && jsonElement.isJsonObject()) {
                            JsonElement packElement = jsonElement.getAsJsonObject()
                                    .get("pack");
                            if (packElement != null && packElement.isJsonObject()) {
                                JsonElement descriptionElement = packElement.getAsJsonObject()
                                        .get("description");
                                if (descriptionElement != null && descriptionElement.isJsonPrimitive()) {
                                    packDescription = descriptionElement.getAsString();
                                }
                            }
                        }
                        if (packDescription != null) {
                            Path repositoryPath = newDirectory.getParentFile()
                                    .toPath();
                            project.getAdditionalPackLocations()
                                    .put(packDescription, repositoryPath);
                            this.editorItemService.addPackRepositoryItem(packDescription, repositoryPath);
                        }
                    } catch (IOException ioException) {
                        LOGGER.error("Failed to read pack.mcmeta", ioException);
                        ExceptionDialog exceptionDialog = new ExceptionDialog(ioException);
                        exceptionDialog.showAndWait();
                    }
                } else {
                    new Alert(AlertType.ERROR, "Did not find valid pack.mcmeta");
                }
            } else {
                new Alert(AlertType.ERROR, "Did not find valid folder for Pack")
                        .showAndWait();
            }
        }

    }

    @FXML
    private void addPackRepository(ActionEvent ignored) {
        new ExceptionDialog(new UnsupportedOperationException())
                .showAndWait();
    }
}
