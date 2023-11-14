package xyz.brassgoggledcoders.minescribe.editor.message;

import javafx.scene.image.Image;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.util.Objects;

public enum MessageType {
    INFO("message/info.png"),
    WARNING("message/warning.png"),
    ERROR("message/error.png");

    private final Image image;

    MessageType(String iconName) {
        this.image = new Image(Objects.requireNonNull(Application.class.getResourceAsStream("icon/" + iconName)));
    }

    public Image getImage() {
        return this.image;
    }
}
