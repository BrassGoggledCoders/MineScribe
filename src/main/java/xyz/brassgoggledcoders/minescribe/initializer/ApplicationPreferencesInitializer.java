package xyz.brassgoggledcoders.minescribe.initializer;

import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.event.StageReadyEvent;
import xyz.brassgoggledcoders.minescribe.preferences.ApplicationPreferences;
import xyz.brassgoggledcoders.minescribe.service.ApplicationPreferencesService;

@Component
public class ApplicationPreferencesInitializer implements ApplicationListener<StageReadyEvent> {
    private final ApplicationPreferencesService applicationPreferencesService;

    @Autowired
    public ApplicationPreferencesInitializer(ApplicationPreferencesService applicationPreferencesService) {
        this.applicationPreferencesService = applicationPreferencesService;
    }


    @Order(50)
    @Override
    public void onApplicationEvent(@NotNull StageReadyEvent event) {
        ApplicationPreferences applicationPreferences = applicationPreferencesService.getApplicationPreferences();
        Stage stage = event.getStage();

        stage.setHeight(applicationPreferences.getHeight());
        stage.setWidth(applicationPreferences.getWidth());

        if (applicationPreferences.getXPos() != Double.MIN_NORMAL) {
            stage.setX(applicationPreferences.getXPos());
        }
        if (applicationPreferences.getYPos() != Double.MIN_NORMAL) {
            stage.setY(applicationPreferences.getYPos());
        }
        
        applicationPreferences.subscribeTo(stage);
    }
}
