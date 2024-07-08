package xyz.brassgoggledcoders.minescribe.initializer;

import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.event.ApplicationReadyEvent;
import xyz.brassgoggledcoders.minescribe.event.ProjectOpenedEvent;
import xyz.brassgoggledcoders.minescribe.preferences.ApplicationPreferences;
import xyz.brassgoggledcoders.minescribe.service.preferences.ApplicationPreferencesService;

@Component
public class ApplicationPreferencesInitializer {
    private final ApplicationPreferencesService applicationPreferencesService;

    @Autowired
    public ApplicationPreferencesInitializer(ApplicationPreferencesService applicationPreferencesService) {
        this.applicationPreferencesService = applicationPreferencesService;
    }


    @Order(50)
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(@NotNull ApplicationReadyEvent event) {
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

    @EventListener(ProjectOpenedEvent.class)
    public void onProjectOpenedEvent(@NotNull ProjectOpenedEvent event) {
        this.applicationPreferencesService.getApplicationPreferences()
                .setLastProject(event.getPath());
    }
}
