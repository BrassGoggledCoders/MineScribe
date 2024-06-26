package xyz.brassgoggledcoders.minescribe.service;

import atlantafx.base.theme.Theme;
import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Setting;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.event.ThemePreferencesChangedEvent;
import xyz.brassgoggledcoders.minescribe.preferences.ThemePreferences;
import xyz.brassgoggledcoders.minescribe.util.PlatformFuture;

import java.util.concurrent.ExecutionException;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings("unused")
public class UserPreferencesService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesService.class);

    private final ApplicationContext applicationContext;

    private final ThemePreferences themePreferences;

    private PreferencesFx preferencesFx;

    @Autowired
    public UserPreferencesService(ApplicationContext context) {
        this.applicationContext = context;

        this.themePreferences = new ThemePreferences(
                new SimpleObjectProperty<>()
        );

        this.themePreferences.theme()
                .addListener((observable, oldValue, newValue) -> this.onThemePreferencesChanged());
    }

    public void loadPreferences() {
        if (Platform.isFxApplicationThread()) {
            this.getPreferencesFx();
        } else {
            try {
                PlatformFuture.getFuture(this::getPreferencesFx)
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Couldn't load preferences", e);
            }
        }

    }

    private PreferencesFx getPreferencesFx() {
        if (this.preferencesFx == null) {
            this.preferencesFx = PreferencesFx.of(
                            UserPreferencesService.class,
                            Category.of(
                                    "Theming",
                                    Setting.of(
                                            "Theme",
                                            FXCollections.observableArrayList(ThemeService.PROJECT_THEMES.stream()
                                                    .map(Theme::getName)
                                                    .toList()
                                            ),
                                            this.themePreferences.theme()
                                    )
                            )
                    )
                    .persistApplicationState(true)
                    .persistWindowState(true)
                    .saveSettings(true)
                    .instantPersistent(true);
        }
        return this.preferencesFx;
    }

    public ThemePreferences getThemePreferences() {
        return this.themePreferences;
    }

    public void openSettings() {
        this.getPreferencesFx().show(true);
    }

    private void onThemePreferencesChanged() {
        this.applicationContext.publishEvent(new ThemePreferencesChangedEvent(this.themePreferences));
    }
}
