package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataRequest;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.CloseTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorTreeCell;
import xyz.brassgoggledcoders.minescribe.editor.server.MineScribeNettyServer;

import java.util.UUID;

public class EditorController {
    @FXML
    public SplitPane editor;

    @FXML
    public ScrollPane files;

    @FXML
    public TabPane editorTabPane;

    @FXML
    public void initialize() {
        TreeView<EditorItem> treeView = new TreeView<>(FileHandler.getInstance().getRootModel());
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new EditorTreeCell());

        this.files.setContent(treeView);

        MineScribeNettyServer.getInstance()
                .sendToClient(new InstanceDataRequest());

        editor.addEventHandler(OpenTabEvent.OPEN_TAB_EVENT_TYPE, this::handleTabOpen);
        editor.addEventHandler(CloseTabEvent.EVENT_TYPE, this::handleTabClose);
    }

    private void handleTabOpen(OpenTabEvent<?> event) {
        UUID tabId = UUID.randomUUID();
        Node node = event.createTabContent(tabId);
        Tab tab = new Tab(event.getTabName());
        tab.setId(tabId.toString());
        this.editorTabPane.getTabs().add(tab);
        tab.setContent(node);
        this.editorTabPane.getSelectionModel().select(tab);
    }

    private void handleTabClose(CloseTabEvent event) {
        this.editorTabPane.getTabs()
                .removeIf(childTab -> childTab.getId().equals(event.getId()));
    }
}
