package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ToolWindowButton extends Button {
    private final ObjectProperty<ToolWindowLocation> location;
    private final ToolWindow toolWindow;

    public ToolWindowButton(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.location = new SimpleObjectProperty<>(this, "location");
        this.location.bindBidirectional(toolWindow.locationProperty());
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

    public final ObjectProperty<ToolWindowLocation> locationProperty() {
        return this.location;
    }

    public ToolWindowLocation getLocation() {
        return this.location.get();
    }

    public void setLocation(ToolWindowLocation location) {
        this.location.set(location);
    }
}
