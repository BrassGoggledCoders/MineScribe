package xyz.brassgoggledcoders.minescribe.editor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.editor.controller.element.InfoPaneController;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.IFileEditorController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.CloseTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorTreeCell;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.FormFileEditorItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditorController {
    private final Logger LOGGER = LoggerFactory.getLogger(EditorController.class);
    private final Gson GSON = new GsonBuilder()
            .create();

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

    @FXML
    public void initialize() {
        FileHandler.initialize();
        files.setRoot(FileHandler.getInstance()
                .getRootModel()
        );
        files.setCellFactory(param -> new EditorTreeCell());

        this.infoPaneController.setParentPane(this.parentPane);

        editor.addEventHandler(OpenTabEvent.OPEN_TAB_EVENT_TYPE, this::handleTabOpen);
        editor.addEventHandler(CloseTabEvent.EVENT_TYPE, this::handleTabClose);

        Project project = InfoRepository.getInstance().getValue(Project.KEY);
        if (project != null) {
            List<Path> tabPaths = new ArrayList<>(project.getOpenTabs().values());
            project.getOpenTabs().clear();
            for (Path openTab : tabPaths) {
                TreeItem<EditorItem> treeItem = FileHandler.getInstance()
                        .getClosestNode(openTab, true);
                if (treeItem != null && treeItem.getValue() instanceof FormFileEditorItem fileEditorItem) {
                    fileEditorItem.openTabFor(editor::fireEvent);
                }
            }
        }
        this.editorTabPane.getTabs()
                .addListener((ListChangeListener<Tab>) c -> {
                    Project currentProject = InfoRepository.getInstance().getValue(Project.KEY);
                    if (currentProject != null) {
                        while (c.next()) {
                            if (c.wasRemoved()) {
                                for (Tab removedTab : c.getRemoved()) {
                                    if (removedTab.getId() != null) {
                                        currentProject.removeOpenTab(UUID.fromString(removedTab.getId()));
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void handleTabOpen(OpenTabEvent<?> event) {
        UUID tabId = UUID.randomUUID();
        Pair<Object, Object> tabContent = event.createTabContent(tabId);
        Tab newTab = null;
        if (tabContent.getFirst() instanceof Tab tab) {
            newTab = tab;
            newTab.setText(event.getTabName());
        } else if (tabContent.getFirst() instanceof Node node) {
            newTab = new Tab(event.getTabName());
            newTab.setContent(node);
        }
        if (newTab != null) {
            newTab.setId(tabId.toString());
            this.editorTabPane.getTabs().add(newTab);

            if (tabContent.getSecond() instanceof IFileEditorController fileEditorController) {
                Project project = InfoRepository.getInstance().getValue(Project.KEY);
                if (project != null && fileEditorController.getPath() != null) {
                    project.addOpenTab(tabId, fileEditorController.getPath());
                }
            }
            this.editorTabPane.getSelectionModel().select(newTab);
        }

    }

    private void handleTabClose(CloseTabEvent event) {
        this.editorTabPane.getTabs()
                .removeIf(childTab -> childTab.getId().equals(event.getId()));
    }

    @FXML
    private void addPack(ActionEvent ignored) {
        Project project = InfoRepository.getInstance()
                .getValue(Project.KEY);
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
                            FileHandler.getInstance()
                                    .addPackRepository(packDescription, repositoryPath);
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
