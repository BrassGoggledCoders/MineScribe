package xyz.brassgoggledcoders.minescribe.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.event.SavePreferencesEvent;
import xyz.brassgoggledcoders.minescribe.preferences.ToolWindowPreferences;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.IToolWindowInfoHandler;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowLocation;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ToolWindowPreferencesService implements IToolWindowInfoHandler {
    private final ObjectProperty<ToolWindowPreferences> toolWindowPreferences;

    public ToolWindowPreferencesService() {
        this.toolWindowPreferences = new SimpleObjectProperty<>(this, "toolWindowPreferences", ToolWindowPreferences.load());
    }

    @NotNull
    public ToolWindowPreferences getToolWindowPreferences() {
        return toolWindowPreferences.get();
    }

    @EventListener(SavePreferencesEvent.class)
    public void saveValues(SavePreferencesEvent ignoredEvent) {
        this.getToolWindowPreferences()
                .trySave();
    }

    @Override
    @Nullable
    public ToolWindowLocation getToolWindowLocation(String name) {
        return this.getToolWindowPreferences()
                .getToolWindowLocations()
                .get(name);
    }

    @Override
    public void setToolWindowLocation(String name, ToolWindowLocation location) {
        this.getToolWindowPreferences()
                .getToolWindowLocations()
                .put(name, location);
    }
}
