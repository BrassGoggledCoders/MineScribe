package xyz.brassgoggledcoders.minescribe.event;

import org.springframework.context.ApplicationEvent;
import xyz.brassgoggledcoders.minescribe.preferences.ThemePreferences;

public class ThemePreferencesChangedEvent extends ApplicationEvent {
    private final ThemePreferences preferences;

    public ThemePreferencesChangedEvent(ThemePreferences themePreferences) {
        super(themePreferences);
        this.preferences = themePreferences;
    }

    public ThemePreferences getThemePreferences() {
        return preferences;
    }
}
