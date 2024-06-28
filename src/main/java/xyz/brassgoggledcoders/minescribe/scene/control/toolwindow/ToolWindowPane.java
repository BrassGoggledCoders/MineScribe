package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

@SuppressWarnings("unused")
@DefaultProperty("content")
public class ToolWindowPane extends BorderPane {
    private final Logger LOGGER = LoggerFactory.getLogger(ToolWindowPane.class);

    private final EnumMap<ToolWindowLocation, ToolWindowToolBar> toolBars = new EnumMap<>(ToolWindowLocation.class);

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    private final ListProperty<ToolWindow> toolWindows = new SimpleListProperty<>(this, "toolWindows", FXCollections.observableArrayList());
    private final ObjectProperty<ToolWindowLocationHandler> toolWindowLocationLoader = new SimpleObjectProperty<>(this, "toolWindowLocationLoader");

    public ToolWindowPane() {
        this.getStyleClass().add("tool-window-pane");
        VBox leftToolBars = new VBox();
        VBox rightToolBars = new VBox();
        leftToolBars.getStyleClass()
                .add("tool-button-pane");
        rightToolBars.getStyleClass()
                .add("tool-button-pane");

        for (ToolWindowLocation toolWindowLocation : ToolWindowLocation.values()) {
            ToolWindowToolBar toolWindowToolBar = new ToolWindowToolBar(toolWindowLocation);

            if (toolWindowLocation.isGrow()) {
                VBox.setVgrow(toolWindowToolBar, Priority.ALWAYS);
            }

            if (toolWindowLocation.isLeft()) {
                leftToolBars.getChildren()
                        .add(toolWindowToolBar);
                if (toolWindowLocation.isTop()) {
                    leftToolBars.getChildren()
                            .add(new Separator());
                }
            } else {
                rightToolBars.getChildren()
                        .add(toolWindowToolBar);
                if (toolWindowLocation.isTop()) {
                    rightToolBars.getChildren()
                            .add(new Separator());
                }
            }

            this.toolBars.put(toolWindowLocation, toolWindowToolBar);
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
                        .forEach(toolWindow -> {
                            if (toolWindow.getLocation() != null) {
                                ToolWindowToolBar toolWindowToolBar = this.toolBars.get(toolWindow.getLocation());
                                toolWindowToolBar.getItems()
                                        .add(new ToolWindowButton(toolWindow));
                            }
                        });
            }
        }
    }
}
