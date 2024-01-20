package xyz.brassgoggledcoders.minescribe.editor.controller.element;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageHandler;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.message.MineScribeMessage;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.scene.table.MessageTypeCell;

import java.nio.file.Path;

public class InfoPaneController {

    private final Provider<Project> projectProvider;

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

    @Inject
    public InfoPaneController(Provider<Project> projectProvider) {
        this.projectProvider = projectProvider;
    }

    @FXML
    public void initialize() {
        this.typeColumn.setCellFactory(param -> new MessageTypeCell<>());
        this.typeColumn.setCellValueFactory(param -> param.getValue().messageTypeProperty());
        this.fieldColumn.setCellValueFactory(param -> param.getValue().fieldProperty());
        this.messageColumn.setCellValueFactory(param -> param.getValue().messageProperty());
        this.pathColumn.setCellValueFactory(param -> param.getValue()
                .filePathProperty()
                .map(file -> projectProvider.get()
                        .getRootPath()
                        .relativize(file)
                )
        );
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
            if (this.parentPane.getDividers().get(0).getPosition() > 0.94) {
                this.parentPane.setDividerPosition(0, 0.75);
            } else {
                this.parentPane.setDividerPosition(0, 0.98);
            }
        } else {
            this.messageView.setPrefHeight(100);
        }
    }
}
