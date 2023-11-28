package xyz.brassgoggledcoders.minescribe.editor.util;

import org.controlsfx.dialog.ExceptionDialog;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class EditorSettings {
    private static final Preferences SETTINGS = Preferences.userNodeForPackage(Application.class)
            .node("settings");

    @Nullable
    public static String getThemeName() {
        return SETTINGS.get("theme", null);
    }

    public static void setThemeName(String themeName) {
        try {
            SETTINGS.put("theme", themeName);
            SETTINGS.flush();
        } catch (BackingStoreException e) {
            ExceptionDialog dialog = new ExceptionDialog(e);
            dialog.setHeaderText("Failed to save theme");
            dialog.showAndWait();
        }
    }
}
