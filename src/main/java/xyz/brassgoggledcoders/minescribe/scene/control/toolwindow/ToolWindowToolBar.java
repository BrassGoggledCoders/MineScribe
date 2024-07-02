package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class ToolWindowToolBar extends ToolBar {
    private final Logger LOGGER = LoggerFactory.getLogger(ToolWindowPane.class);

    private final ObjectProperty<ToolWindowLocation> location;
    private final ToggleGroup group;
    private final BiConsumer<ToolWindowLocation, Node> updateSelected;

    public ToolWindowToolBar(ToolWindowLocation location, BiConsumer<ToolWindowLocation, Node> updateSelected) {
        this.location = new SimpleObjectProperty<>(this, "location", location);
        this.group = new ToggleGroup();
        this.updateSelected = updateSelected;
        this.setOrientation(Orientation.VERTICAL);

        this.group.selectedToggleProperty()
                .addListener(this::updateToggle);

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
                toolWindowButton.setToggleGroup(null);
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
                this.getItems()
                        .add(new ToolWindowButton(toolWindowButton.getToolWindow()));
                dragEvent.consume();
            }
        });

        this.getItems().addListener(this::itemListener);
    }

    private void itemListener(ListChangeListener.Change<? extends Node> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList()
                        .forEach(node -> {
                            if (node instanceof ToolWindowButton toolWindowButton) {
                                toolWindowButton.setToggleGroup(this.group);
                                toolWindowButton.setLocation(this.getLocation());
                            }
                        });
            }
        }
    }

    private void updateToggle(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (newValue instanceof ToolWindowButton toolWindowButton) {
            this.updateSelected.accept(this.getLocation(), toolWindowButton.getContent());
        } else {
            this.updateSelected.accept(this.getLocation(), null);
        }
    }

    public final ObjectProperty<ToolWindowLocation> locationProperty() {
        return this.location;
    }

    public final ToolWindowLocation getLocation() {
        return this.locationProperty()
                .getValue();
    }
}
