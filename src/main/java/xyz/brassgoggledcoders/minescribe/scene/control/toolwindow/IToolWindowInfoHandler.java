package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

import org.jetbrains.annotations.Nullable;

public interface IToolWindowInfoHandler {
    @Nullable
    ToolWindowLocation getToolWindowLocation(String name);

    void setToolWindowLocation(String name, ToolWindowLocation location);
}
