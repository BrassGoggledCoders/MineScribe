package xyz.brassgoggledcoders.minescribe.preferences;

import atlantafx.base.theme.Theme;
import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Setting;
import javafx.collections.FXCollections;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.theme.ThemeManager;

public class MineScribePreferences {
    private static final ThemePreferences THEME_PREFERENCES = new ThemePreferences();

    private static final PreferencesFx PREFERENCES_FX = PreferencesFx.of(
                    MineScribePreferences.class,
                    Category.of(
                            "Theming",
                            Setting.of(
                                    "Theme",
                                    FXCollections.observableArrayList(ThemeManager.getInstance()
                                            .getThemes()
                                            .stream()
                                            .map(Theme::getName)
                                            .toList()
                                    ),
                                    THEME_PREFERENCES.theme()
                            )
                    )
            )
            .persistApplicationState(true)
            .persistWindowState(true)
            .saveSettings(true)
            .instantPersistent(true);

    public static ThemePreferences getThemePreferences() {
        return THEME_PREFERENCES;
    }

    public static void openSettings() {
        PREFERENCES_FX.show(true);
    }
}
