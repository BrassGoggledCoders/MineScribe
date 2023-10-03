package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataRequest;
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
    public void initialize() {
        TreeView<EditorItem> treeView = new TreeView<>(FileHandler.getInstance().getRootModel());
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new EditorTreeCell());

        this.files.setContent(treeView);

        MineScribeNettyServer.getInstance()
                .sendToClient(new InstanceDataRequest());
    }
}
