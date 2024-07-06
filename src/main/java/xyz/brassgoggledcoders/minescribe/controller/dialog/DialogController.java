package xyz.brassgoggledcoders.minescribe.controller.dialog;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import xyz.brassgoggledcoders.minescribe.event.SceneReadyEvent;

public abstract class DialogController {
    private final ApplicationContext applicationContext;

    public DialogController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        Stage stage = this.getStage();
        if (stage != null) {
            stage.sceneProperty()
                    .subscribe(scene -> {
                        if (scene != null) {
                            this.applicationContext.publishEvent(new SceneReadyEvent(scene));
                        }
                    });
        }
    }

    protected abstract Stage getStage();
}
