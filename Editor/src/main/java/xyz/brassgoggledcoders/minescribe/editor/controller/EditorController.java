package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataRequest;
import xyz.brassgoggledcoders.minescribe.editor.event.TabEvent.OpenTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorTreeCell;
import xyz.brassgoggledcoders.minescribe.editor.server.MineScribeNettyServer;

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
    }

    private void handleTabOpen(OpenTabEvent<?> event) {
        Node node = event.createTab();
        Tab tab = new Tab(event.getTabName());

        this.editorTabPane.getTabs().add(tab);
        tab.setContent(node);
        this.editorTabPane.getSelectionModel().select(tab);
    }
}
