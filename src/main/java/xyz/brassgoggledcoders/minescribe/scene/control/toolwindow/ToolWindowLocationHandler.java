package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

public interface ToolWindowLocationHandler {
    ToolWindowLocation getToolWindowLocation(String name);

    void updatedToolWindowLocation(String name, ToolWindowLocation location);
}
