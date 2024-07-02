package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

@DefaultProperty("graphic")
public class ToolWindow extends Labeled {
    private final ObjectProperty<ToolWindowLocation> location = new SimpleObjectProperty<>(this, "location", ToolWindowLocation.LEFT_TOP);
    private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");

    public ToolWindow() {
        this.tooltipProperty()
                .bind(this.textProperty()
                        .map(text -> {
                            Tooltip toolTip = new Tooltip(text);
                            toolTip.setShowDelay(Duration.seconds(1));
                            return toolTip;
                        })
                );
    }

    public final ObjectProperty<ToolWindowLocation> locationProperty() {
        return location;
    }

    public final ToolWindowLocation getLocation() {
        return this.location.get();
    }

    public final void setLocation(final ToolWindowLocation location) {
        this.location.set(location);
    }

    public final ObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public Node getContent() {
        return this.content.getValue();
    }

    @Override
    public Skin<ToolWindow> createDefaultSkin() {
        return new ToolWindowSkin(this);
    }
}
