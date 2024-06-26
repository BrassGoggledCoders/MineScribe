package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Iterator;

@SuppressWarnings("unused")
@DefaultProperty("content")
public class ToolWindowPane extends BorderPane {
    private final Logger LOGGER = LoggerFactory.getLogger(ToolWindowPane.class);

    private final EnumMap<ToolWindowLocation, ToolBar> toolBars = new EnumMap<>(ToolWindowLocation.class);

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    private final ListProperty<ToolWindow> toolWindows = new SimpleListProperty<>(this, "toolWindows", FXCollections.observableArrayList());
    private final ObjectProperty<ToolWindowLocationHandler> toolWindowLocationLoader = new SimpleObjectProperty<>(this, "toolWindowLocationLoader");

    public ToolWindowPane() {
        VBox leftToolBars = new VBox();
        VBox rightToolBars = new VBox();

        for (ToolWindowLocation toolWindowLocation : ToolWindowLocation.values()) {
            ToolBar toolBar = new ToolBar();
            toolBar.setOrientation(Orientation.VERTICAL);

            if (toolWindowLocation.isGrow()) {
                VBox.setVgrow(toolBar, Priority.ALWAYS);
            }

            if (toolWindowLocation.isLeft()) {
                leftToolBars.getChildren()
                        .add(toolBar);
                if (toolWindowLocation.isTop()) {
                    leftToolBars.getChildren()
                            .add(new Separator());
                }
            } else {
                rightToolBars.getChildren()
                        .add(toolBar);
                if (toolWindowLocation.isTop()) {
                    rightToolBars.getChildren()
                            .add(new Separator());
                }
            }

            toolBar.setOnDragEntered(dragEvent -> {
                if (dragEvent.getGestureSource() instanceof ToolWindowButton) {
                    LOGGER.info("Entered {}", toolWindowLocation);
                    dragEvent.consume();
                }
            });

            toolBar.setOnDragExited(dragEvent -> {
                if (dragEvent.getGestureSource() instanceof ToolWindowButton toolWindowButton) {
                    toolBar.getItems()
                            .remove(toolWindowButton);
                    LOGGER.info("Exited {}", toolWindowLocation);
                    dragEvent.consume();
                }
            });

            toolBar.setOnDragOver(dragEvent -> {
                if (dragEvent.getGestureSource() instanceof ToolWindowButton toolWindowButton) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                    dragEvent.consume();
                }
            });

            toolBar.setOnDragDropped(dragEvent -> {
                LOGGER.info("Drag Dropped {}", toolWindowLocation);
                if (dragEvent.getGestureSource() instanceof ToolWindowButton toolWindowButton) {
                    toolWindowButton.getToolWindow()
                            .setLocation(toolWindowLocation);
                    this.addToolWindowToLocation(toolWindowButton.getToolWindow());
                    dragEvent.consume();
                }
            });

            this.toolBars.put(toolWindowLocation, toolBar);
        }

        this.setLeft(leftToolBars);
        this.setRight(rightToolBars);

        this.toolWindowLocationLoader.subscribe(this::handlerLoaderChange);
        this.toolWindows.addListener(this::handleToolWindowChange);
    }

    public final ListProperty<ToolWindow> toolWindowsProperty() {
        return this.toolWindows;
    }

    public final ObservableList<ToolWindow> getToolWindows() {
        return this.toolWindows.get();
    }

    public final Node getContent() {
        return this.content.get();
    }

    public final void setContent(Node node) {
        this.content.setValue(node);
    }

    public final ObjectProperty<Node> contentProperty() {
        return this.content;
    }

    public ToolWindowLocationHandler getToolWindowLocationLoader() {
        return this.toolWindowLocationLoader.get();
    }

    public void setToolWindowLocationLoader(ToolWindowLocationHandler toolWindowLocationHandler) {
        this.toolWindowLocationLoader.set(toolWindowLocationHandler);
    }

    public void handlerLoaderChange(ToolWindowLocationHandler toolWindowLocationHandler) {
        if (toolWindowLocationHandler != null) {
            for (ToolWindow toolWindow : this.getToolWindows()) {
                String text = toolWindow.getText();
                if (text != null) {
                    ToolWindowLocation toolWindowLocation = toolWindowLocationHandler.getToolWindowLocation(text);
                    if (toolWindowLocation != null) {
                        toolWindow.setLocation(toolWindowLocation);
                    }
                }
            }
        }
    }

    public void handleToolWindowChange(Change<? extends ToolWindow> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList()
                        .forEach(this::addToolWindowToLocation);
            }
        }
    }

    public void addToolWindowToLocation(ToolWindow toolWindow) {
        boolean alreadyAdded = false;
        for (ToolWindowLocation toolWindowLocation : ToolWindowLocation.values()) {
            ToolBar toolBar = toolBars.get(toolWindowLocation);
            Iterator<Node> toolBarItems = toolBar.getItems()
                    .iterator();

            while (toolBarItems.hasNext()) {
                Node toolBarItem = toolBarItems.next();
                if (toolBarItem instanceof ToolWindowButton toolWindowButton) {
                    if (toolWindowButton.getToolWindow() == toolWindow) {
                        //TODO: Handle this for Drag and Drop
                        if (toolWindowLocation == toolWindow.getLocation()) {
                            alreadyAdded = true;
                        } else {
                            toolBarItems.remove();
                        }
                    }
                }
            }
        }
        if (!alreadyAdded) {
            ToolWindowLocation location = toolWindow.getLocation();
            int offsets = 1;

            ObservableList<Node> items = toolBars.get(location)
                    .getItems();

            while (items.size() < offsets) {
                offsets -= 1;
            }
            items.add(items.size() - offsets, new ToolWindowButton(toolWindow));
        }
    }
}
