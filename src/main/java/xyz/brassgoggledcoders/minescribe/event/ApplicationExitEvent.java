package xyz.brassgoggledcoders.minescribe.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class ApplicationExitEvent extends ApplicationEvent {
    public ApplicationExitEvent(Stage stage) {
        super(stage);
    }
}
