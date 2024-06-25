package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import javafx.scene.control.Button;

public class ToolWindowButton extends Button {
    private final ToolWindow toolWindow;

    public ToolWindowButton(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.graphicProperty()
                .bind(toolWindow.graphicProperty());
        this.tooltipProperty()
                .bind(toolWindow.tooltipProperty());
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }
}
