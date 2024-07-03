package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class ToolWindowPane extends BorderPane {
    private final ToolWindow toolWindow;

    private final GridPane titlePane;

    public ToolWindowPane(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

        this.titlePane = new GridPane();
        this.titlePane.getStyleClass()
                .add("tool-window-title");
        this.toolWindow.textProperty()
                .subscribe(this::handleTitleChange);
        this.setTop(this.titlePane);

        this.toolWindow.contentProperty()
                .subscribe(this::handleContentChange);
    }

    private void handleTitleChange(String text) {
        Label label = new Label(text);
        this.titlePane.add(label, 0, 0);
        GridPane.setHalignment(label, HPos.LEFT);
    }

    private void handleContentChange(Node node) {
        this.setCenter(node);
        this.requestLayout();
    }
}
