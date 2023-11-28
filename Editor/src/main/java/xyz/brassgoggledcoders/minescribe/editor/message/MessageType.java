package xyz.brassgoggledcoders.minescribe.editor.message;

import org.kordamp.ikonli.feather.Feather;

public enum MessageType {
    INFO("info", Feather.INFO),
    WARNING("warning", Feather.ALERT_TRIANGLE),
    ERROR("error", Feather.ALERT_OCTAGON);

    private final Feather feather;
    private final String name;

    MessageType(String name, Feather feather) {
        this.name = name;
        this.feather = feather;
    }

    public String getName() {
        return this.name;
    }

    public Feather getFeather() {
        return this.feather;
    }
}
