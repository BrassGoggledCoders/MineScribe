package xyz.brassgoggledcoders.minescribe.initializer;

import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.event.SavePreferencesEvent;
import xyz.brassgoggledcoders.minescribe.event.StageReadyEvent;
import xyz.brassgoggledcoders.minescribe.util.EventScheduledService;

@Component
public class ScheduleInitializer {

    private final ApplicationContext applicationContext;

    @Autowired
    public ScheduleInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener(StageReadyEvent.class)
    public void stageReady(StageReadyEvent ignoredEvent) {
        EventScheduledService<SavePreferencesEvent> scheduledService = new EventScheduledService<>(
                this.applicationContext,
                () -> new SavePreferencesEvent(this)
        );

        scheduledService.setPeriod(Duration.seconds(5));
        scheduledService.start();
    }
}

