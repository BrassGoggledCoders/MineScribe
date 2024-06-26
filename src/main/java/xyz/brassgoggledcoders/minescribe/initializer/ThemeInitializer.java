package xyz.brassgoggledcoders.minescribe.initializer;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.event.SceneReadyEvent;
import xyz.brassgoggledcoders.minescribe.event.StageReadyEvent;
import xyz.brassgoggledcoders.minescribe.event.ThemePreferencesChangedEvent;
import xyz.brassgoggledcoders.minescribe.service.ThemeService;

@Component
public class ThemeInitializer {
    private final ThemeService themeService;

    @Autowired
    public ThemeInitializer(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Order(0)
    @EventListener(SceneReadyEvent.class)
    public void onSceneReady(@NotNull SceneReadyEvent event) {
        this.themeService.setup(event.getScene());
    }

    @EventListener(ThemePreferencesChangedEvent.class)
    public void onThemePreferencesChanged(ThemePreferencesChangedEvent event) {
        this.themeService.setTheme(event.getThemePreferences()
                .theme()
                .getValue()
        );
    }
}
