package xyz.brassgoggledcoders.minescribe.editor.controller.element;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageHandler;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.message.MineScribeMessage;
import xyz.brassgoggledcoders.minescribe.editor.scene.table.ImageTableCell;

import java.nio.file.Path;

public class InfoPaneController {


    @FXML
    private StackPane infoStackPane;


    @FXML
    private TableView<MineScribeMessage> messageView;
    @FXML
    private TableColumn<MineScribeMessage, MessageType> typeColumn;
    @FXML
    private TableColumn<MineScribeMessage, String> fieldColumn;
    @FXML
    private TableColumn<MineScribeMessage, String> messageColumn;
    @FXML
    public TableColumn<MineScribeMessage, Path> pathColumn;
    private SplitPane parentPane;

    @FXML
    public void initialize() {
        this.typeColumn.setCellFactory(param -> new ImageTableCell<>(MessageType::getImage));
        this.typeColumn.setCellValueFactory(param -> param.getValue().messageTypeProperty());
        this.fieldColumn.setCellValueFactory(param -> param.getValue().fieldProperty());
        this.messageColumn.setCellValueFactory(param -> param.getValue().messageProperty());
        this.pathColumn.setCellValueFactory(param -> param.getValue().filePathProperty());
        this.messageView.setItems(MessageHandler.getInstance()
                .getMessages()
        );
    }

    public void setParentPane(SplitPane scrollPane) {
        this.parentPane = scrollPane;
    }

    @FXML
    public void showMessagePane() {
        this.infoStackPane.getChildren().remove(this.messageView);
        this.infoStackPane.getChildren().add(this.messageView);
        this.messageView.setVisible(true);
        if (this.parentPane != null) {
            if (this.parentPane.getDividers().get(0).getPosition() > 0.95) {
                this.parentPane.setDividerPosition(0, 0.8);
            }
        } else {
            this.messageView.setPrefHeight(100);
        }
    }
}
