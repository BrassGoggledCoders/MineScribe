package xyz.brassgoggledcoders.minescribe.initializer;

import javafx.scene.image.Image;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.event.StageReadyEvent;

@Component
public class StageIconInitializer {

    @EventListener(StageReadyEvent.class)
    public void stageReady(StageReadyEvent event) {
        event.getStage()
                .getIcons()
                .addFirst(new Image("/minescribe/icon/minescribe.png"));
    }
}
