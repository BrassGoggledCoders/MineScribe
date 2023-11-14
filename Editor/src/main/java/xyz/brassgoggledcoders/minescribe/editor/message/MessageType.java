package xyz.brassgoggledcoders.minescribe.editor.message;

import javafx.scene.image.Image;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.util.Objects;

public enum MessageType {
    INFO("info"),
    WARNING("warning"),
    ERROR("error");

    private final Image image;
    private final String name;

    MessageType(String name) {
        this.name = name;
        this.image = new Image(Objects.requireNonNull(
                Application.class.getResourceAsStream("icon/message/" + name + ".png"),
                "Failed to find " + name
        ));
    }

    public String getName() {
        return this.name;
    }

    public Image getImage() {
        return this.image;
    }
}
