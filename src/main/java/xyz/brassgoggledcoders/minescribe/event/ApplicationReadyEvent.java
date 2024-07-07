package xyz.brassgoggledcoders.minescribe.event;

import javafx.stage.Stage;

public class ApplicationReadyEvent extends StageReadyEvent {
    public ApplicationReadyEvent(Stage stage) {
        super(stage);
    }
}
