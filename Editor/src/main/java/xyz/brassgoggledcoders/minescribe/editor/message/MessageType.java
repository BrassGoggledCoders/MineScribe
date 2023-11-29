package xyz.brassgoggledcoders.minescribe.editor.message;

import atlantafx.base.theme.Styles;
import org.kordamp.ikonli.feather.Feather;

public enum MessageType {
    INFO("info", Styles.BG_NEUTRAL_EMPHASIS, Feather.INFO),
    WARNING("warning", Styles.BG_WARNING_EMPHASIS, Feather.ALERT_TRIANGLE),
    ERROR("error", Styles.BG_DANGER_EMPHASIS, Feather.ALERT_OCTAGON);

    private final Feather feather;
    private final String style;
    private final String name;

    MessageType(String name, String style, Feather feather) {
        this.name = name;
        this.style = style;
        this.feather = feather;
    }

    public String getName() {
        return this.name;
    }

    public String getStyle() {
        return style;
    }

    public Feather getFeather() {
        return this.feather;
    }
}
