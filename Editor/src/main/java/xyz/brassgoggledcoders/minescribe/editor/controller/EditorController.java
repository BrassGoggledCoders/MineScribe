package xyz.brassgoggledcoders.minescribe.editor.controller;

import com.mojang.datafixers.util.Pair;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditorController {
    @FXML
    public SplitPane editor;
    @FXML
    public ScrollPane files;
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
        TreeView<EditorItem> treeView = new TreeView<>(FileHandler.getInstance().getRootModel());
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new EditorTreeCell());

        this.files.setContent(treeView);
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
                if (treeItem.getValue() instanceof FormFileEditorItem fileEditorItem) {
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
}
