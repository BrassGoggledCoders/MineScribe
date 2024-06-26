package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ToolWindowButton extends Button {
    private final ToolWindow toolWindow;

    public ToolWindowButton(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.graphicProperty()
                .bind(toolWindow.graphicProperty());
        this.tooltipProperty()
                .bind(toolWindow.tooltipProperty());

        this.setOnDragDetected(dragEvent -> {
            Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);
            dragboard.setDragView(this.snapshot(null, null));
            ClipboardContent content = new ClipboardContent();
            content.putString(this.getText());
            dragboard.setContent(content);
            dragEvent.consume();
        });
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }
}
