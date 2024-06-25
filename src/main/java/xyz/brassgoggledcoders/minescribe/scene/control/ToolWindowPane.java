package xyz.brassgoggledcoders.minescribe.scene.control;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindow;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowLocation;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowLocationHandler;

@SuppressWarnings("unused")
@DefaultProperty("content")
public class ToolWindowPane extends BorderPane {
    private final ToolBar leftTopToolBar;
    private final ToolBar leftBottomToolBar;
    private final ToolBar bottomLeftToolBar;
    private final ToolBar rightToolBar = new ToolBar();

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    private final ListProperty<ToolWindow> toolWindows = new SimpleListProperty<>(this, "toolWindows", FXCollections.observableArrayList());
    private final ObjectProperty<ToolWindowLocationHandler> toolWindowLocationLoader = new SimpleObjectProperty<>(this, "toolWindowLocationLoader");

    public ToolWindowPane() {
        this.leftTopToolBar = new ToolBar();
        this.leftTopToolBar.setOrientation(Orientation.VERTICAL);
        this.leftTopToolBar.getItems()
                .add(new Separator());

        this.leftBottomToolBar = new ToolBar();
        this.leftBottomToolBar.setOrientation(Orientation.VERTICAL);
        VBox.setVgrow(this.leftBottomToolBar, Priority.ALWAYS);

        this.bottomLeftToolBar = new ToolBar();
        this.bottomLeftToolBar.setOrientation(Orientation.VERTICAL);

        VBox leftToolBars = new VBox();
        leftToolBars.getChildren()
                .addAll(
                        this.leftTopToolBar,
                        this.leftBottomToolBar,
                        this.bottomLeftToolBar
                );

        this.setLeft(leftToolBars);

        this.rightToolBar.getItems()
                .add(new Button("", new FontIcon(Material2OutlinedAL.FOLDER)));
        this.rightToolBar.setOrientation(Orientation.VERTICAL);

        this.setRight(this.rightToolBar);

        this.toolWindowLocationLoader.subscribe(this::handlerLoaderChange);
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
}
