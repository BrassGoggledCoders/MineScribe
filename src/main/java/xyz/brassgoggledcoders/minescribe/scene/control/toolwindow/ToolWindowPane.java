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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.Iterator;

@SuppressWarnings("unused")
@DefaultProperty("content")
public class ToolWindowPane extends BorderPane {
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

            if (toolWindowLocation.isTop()) {
                toolBar.getItems()
                        .addLast(new Separator());
            }

            if (toolWindowLocation.isGrow()) {
                VBox.setVgrow(toolBar, Priority.ALWAYS);
            }

            if (toolWindowLocation.isLeft()) {
                leftToolBars.getChildren()
                        .add(toolBar);
            } else {
                rightToolBars.getChildren()
                        .add(toolBar);
            }

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
            if (location.isTop()) {
                offsets = 2;
            }

            ObservableList<Node> items = toolBars.get(location)
                    .getItems();

            while (items.size() < offsets) {
                offsets -= 1;
            }
            items.add(items.size() - offsets, new ToolWindowButton(toolWindow));
        }
    }
}
