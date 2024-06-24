package xyz.brassgoggledcoders.minescribe.scene.control;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

@DefaultProperty("content")
public class ToolWindowPane extends BorderPane {
    private final ToolBar leftTopToolBar;
    private final ToolBar leftBottomToolBar;
    private final ToolBar bottomLeftToolBar;
    private final ToolBar rightToolBar = new ToolBar();

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

    public ToolWindowPane() {
        this.leftTopToolBar = new ToolBar();
        this.leftTopToolBar.setOrientation(Orientation.VERTICAL);
        this.leftTopToolBar.getItems()
                .add(new Button("", new FontIcon(Material2OutlinedAL.FOLDER)));
        this.leftTopToolBar.getItems()
                .add(new Separator());

        this.leftBottomToolBar = new ToolBar();
        this.leftBottomToolBar.setOrientation(Orientation.VERTICAL);
        this.leftBottomToolBar.getItems()
                .add(new Button("", new FontIcon(Material2OutlinedAL.FOLDER)));
        VBox.setVgrow(this.leftBottomToolBar, Priority.ALWAYS);

        this.bottomLeftToolBar = new ToolBar();
        this.bottomLeftToolBar.setOrientation(Orientation.VERTICAL);
        this.bottomLeftToolBar.getItems()
                .add(new Button("", new FontIcon(Material2OutlinedAL.FOLDER)));

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

    }

    public final Node getContent() {
        return this.content.get();
    }

    public final void setContent(Node node) {
        this.content.setValue(node);
    }
}
