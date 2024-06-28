package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.ToolBar;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class ToolWindowToolBar extends ToolBar {
    private final Logger LOGGER = LoggerFactory.getLogger(ToolWindowPane.class);

    private final ObjectProperty<ToolWindowLocation> location;

    public ToolWindowToolBar(ToolWindowLocation location) {
        this.location = new SimpleObjectProperty<>(this, "location", location);
        this.setOrientation(Orientation.VERTICAL);

        this.setOnDragEntered(dragEvent -> {
            if (dragEvent.getGestureSource() instanceof ToolWindowButton) {
                LOGGER.info("Entered {}", this.location.getValue());
                dragEvent.consume();
            }
        });

        this.setOnDragExited(dragEvent -> {
            if (dragEvent.getGestureSource() instanceof ToolWindowButton toolWindowButton) {
                this.getItems()
                        .remove(toolWindowButton);
                toolWindowButton.locationProperty()
                        .setValue(null);
                LOGGER.info("Exited {}", this.location.getValue());
                dragEvent.consume();
            }
        });

        this.setOnDragOver(dragEvent -> {
            if (dragEvent.getGestureSource() instanceof ToolWindowButton) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                dragEvent.consume();
            }
        });

        this.setOnDragDropped(dragEvent -> {
            LOGGER.info("Drag Dropped {}", this.location.getValue());
            if (dragEvent.getGestureSource() instanceof ToolWindowButton toolWindowButton) {
                toolWindowButton.getToolWindow()
                        .setLocation(this.location.get());
                this.getItems()
                        .add(new ToolWindowButton(toolWindowButton.getToolWindow()));
                dragEvent.consume();
            }
        });
    }

    public final ObjectProperty<ToolWindowLocation> locationProperty() {
        return this.location;
    }

}
