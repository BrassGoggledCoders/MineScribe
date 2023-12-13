package xyz.brassgoggledcoders.minescribe.editor.util;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ButtonUtils {
    public static Button createIconButton(Feather feather) {
        FontIcon icon = new FontIcon(feather);
        Button button = new Button("&nbsp;", icon);
        button.getStyleClass().add(Styles.BUTTON_ICON);
        return button;
    }

    public static Button createIconButton(Feather feather, String tooltip) {
        Button button = createIconButton(feather);
        Tooltip buttonToolTip = new Tooltip(tooltip);
        button.setTooltip(buttonToolTip);
        return button;
    }
}
