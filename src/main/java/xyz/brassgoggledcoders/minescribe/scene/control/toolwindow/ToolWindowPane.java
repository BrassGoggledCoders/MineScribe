package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
@DefaultProperty("content")
public class ToolWindowPane extends BorderPane {
    private final Logger LOGGER = LoggerFactory.getLogger(ToolWindowPane.class);

    private final EnumMap<ToolWindowLocation, ToolWindowToolBar> toolBars = new EnumMap<>(ToolWindowLocation.class);

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    private final ListProperty<ToolWindow> toolWindows = new SimpleListProperty<>(this, "toolWindows", FXCollections.observableArrayList());
    private final ObjectProperty<IToolWindowInfoHandler> toolWindowInfoHandler = new SimpleObjectProperty<>(this, "toolWindowInfoHandler");

    private final SplitPane verticalPane = new SplitPane();
    private final SplitPane horizontalPane = new SplitPane();

    private final SplitPane bottomToolWindows = new SplitPane();
    private final SplitPane leftToolWindows = new SplitPane();
    private final SplitPane rightToolWindows = new SplitPane();

    private final AnchorPane contentPane = new AnchorPane();

    public ToolWindowPane() {
        this.getStyleClass().add("tool-window-pane");
        VBox leftToolBars = new VBox();
        VBox rightToolBars = new VBox();
        leftToolBars.getStyleClass()
                .add("tool-button-pane");
        rightToolBars.getStyleClass()
                .add("tool-button-pane");

        for (ToolWindowLocation toolWindowLocation : ToolWindowLocation.values()) {
            ToolWindowToolBar toolWindowToolBar = new ToolWindowToolBar(toolWindowLocation, this::handleWindowUpdate);
            this.toolWindowInfoHandler.bindBidirectional(toolWindowToolBar.toolWindowInfoHandlerProperty());
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

        this.leftToolWindows.setOrientation(Orientation.VERTICAL);
        this.rightToolWindows.setOrientation(Orientation.VERTICAL);

        this.toolWindowInfoHandler.subscribe(this::updateInfoHandler);
        this.toolWindows.addListener(this::handleToolWindowChange);
        this.setCenter(verticalPane);

        this.verticalPane.setOrientation(Orientation.VERTICAL);
        this.verticalPane.getItems()
                .add(this.horizontalPane);

        this.horizontalPane.getItems()
                .add(this.contentPane);

        this.content.addListener(this::contentListener);
    }

    private void handleWindowUpdate(ToolWindowLocation toolWindowLocation, @Nullable Node node) {
        if (node != null) {
            node.getProperties()
                    .put(ToolWindowLocation.KEY, toolWindowLocation);
        }
        if (!toolWindowLocation.isTop() && !toolWindowLocation.isGrow()) {
            removeExisting(this.bottomToolWindows, toolWindowLocation);
            if (node != null) {
                if (this.bottomToolWindows.getParent() == null) {
                    this.verticalPane.getItems()
                            .addLast(this.bottomToolWindows);
                }
                if (toolWindowLocation.isLeft()) {
                    this.bottomToolWindows.getItems()
                            .addFirst(node);
                } else {
                    this.bottomToolWindows.getItems()
                            .addLast(node);
                }
            } else if (this.bottomToolWindows.getItems().isEmpty()) {
                this.verticalPane.getItems()
                        .remove(this.bottomToolWindows);
            }
        } else if (toolWindowLocation.isLeft()) {
            removeExisting(this.leftToolWindows, toolWindowLocation);
            if (node != null) {
                if (this.leftToolWindows.getParent() == null) {
                    this.horizontalPane.getItems()
                            .addFirst(this.leftToolWindows);
                }
                if (toolWindowLocation.isTop()) {
                    this.leftToolWindows.getItems()
                            .addFirst(node);
                } else {
                    this.leftToolWindows.getItems()
                            .addLast(node);
                }
            } else if (this.leftToolWindows.getItems().isEmpty()) {
                this.horizontalPane.getItems()
                        .remove(this.leftToolWindows);
            }
        } else {
            removeExisting(this.rightToolWindows, toolWindowLocation);
            if (node != null) {
                if (this.rightToolWindows.getParent() == null) {
                    this.horizontalPane.getItems()
                            .addLast(this.rightToolWindows);
                }
                if (toolWindowLocation.isTop()) {
                    this.rightToolWindows.getItems()
                            .addFirst(node);
                } else {
                    this.rightToolWindows.getItems()
                            .addLast(node);
                }
            } else if (this.rightToolWindows.getItems().isEmpty()) {
                this.horizontalPane.getItems()
                        .remove(this.rightToolWindows);
            }
        }
    }

    private void removeExisting(SplitPane toolWindows, ToolWindowLocation toolWindowLocation) {
        Iterator<Node> childNodes = toolWindows.getItems()
                .iterator();
        while (childNodes.hasNext()) {
            Node childNode = childNodes.next();
            Map<Object, Object> properties = childNode.getProperties();
            if (properties.get(ToolWindowLocation.KEY) == toolWindowLocation) {
                properties.remove(ToolWindowLocation.KEY);
                childNodes.remove();
            }
        }
    }

    private void contentListener(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != newValue) {
            if (oldValue != null) {
                this.contentPane.getChildren()
                        .remove(oldValue);
            }
            if (newValue != null) {
                AnchorPane.setTopAnchor(newValue, 0.0);
                AnchorPane.setLeftAnchor(newValue, 0.0);
                AnchorPane.setRightAnchor(newValue, 0.0);
                AnchorPane.setBottomAnchor(newValue, 0.0);
                this.contentPane.getChildren()
                        .add(newValue);
            }
            this.contentPane.requestLayout();
        }
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

    public IToolWindowInfoHandler getToolWindowInfoHandler() {
        return this.toolWindowInfoHandler.get();
    }

    public void setToolWindowInfoHandler(IToolWindowInfoHandler toolWindowLocationHandler) {
        this.toolWindowInfoHandler.set(toolWindowLocationHandler);
    }

    public void updateInfoHandler(IToolWindowInfoHandler toolWindowLocationHandler) {
        if (toolWindowLocationHandler != null) {
            for (ToolWindow toolWindow : this.getToolWindows()) {
                String text = toolWindow.getText();
                if (text != null) {
                    ToolWindowLocation toolWindowLocation = toolWindowLocationHandler.getToolWindowLocation(text);
                    if (toolWindowLocation != null) {
                        ToolWindowToolBar toolWindowToolBar = this.toolBars.get(toolWindowLocation);
                        if (toolWindowToolBar != null) {
                            ToolWindowLocation previousLocation = toolWindow.getLocation();
                            if (previousLocation != null) {
                                ToolWindowToolBar previousToolBar = this.toolBars.get(previousLocation);
                                if (previousToolBar != null) {
                                    previousToolBar.getItems()
                                            .removeIf(node -> {
                                                if (node instanceof ToolWindowButton toolWindowButton) {
                                                    return toolWindowButton.getToolWindow() == toolWindow;
                                                }

                                                return false;
                                            });
                                }
                            }
                            toolWindowToolBar.getItems()
                                    .add(new ToolWindowButton(toolWindow));
                        }
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
