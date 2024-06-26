package xyz.brassgoggledcoders.minescribe.event;

import javafx.scene.Scene;
import org.springframework.context.ApplicationEvent;

public class SceneReadyEvent extends ApplicationEvent {
    private final Scene scene;

    public SceneReadyEvent(Scene scene) {
        super(scene);
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
