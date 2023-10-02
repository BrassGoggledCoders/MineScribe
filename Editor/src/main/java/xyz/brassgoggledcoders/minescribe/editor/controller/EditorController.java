package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.FolderLocationRequest;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.server.MineScribeNettyServer;

import java.nio.file.Path;

public class EditorController {
    @FXML
    public SplitPane editor;

    @FXML
    public ScrollPane files;

    @FXML
    public void initialize() {
        TreeView<Path> treeView = new TreeView<>(FileHandler.getInstance().getModel().getRoot());
        treeView.setShowRoot(false);

        FileHandler.getInstance()
                .getModel()
                .modifications()
                .subscribe(m -> {
                    if (m.getInitiator() == FileHandler.ChangeSource.EXTERNAL) {
                        FileHandler.getInstance().reload(m.getPath());
                    }
                });

        this.files.setContent(treeView);

        MineScribeNettyServer.getInstance()
                .sendToClient(new FolderLocationRequest());
    }
}
