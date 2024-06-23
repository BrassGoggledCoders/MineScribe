package xyz.brassgoggledcoders.minescribe.preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import xyz.brassgoggledcoders.minescribe.theme.ThemeManager;

public record ThemePreferences(
        ObjectProperty<String> theme
) {
    public ThemePreferences() {
        this(
                new SimpleObjectProperty<>(ThemeManager.getInstance()
                        .getThemeName()
                )
        );
    }
}
