package xyz.brassgoggledcoders.minescribe.util;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import java.util.function.Supplier;

public class EventScheduledService<T extends ApplicationEvent> extends ScheduledService<Void> {
    private final ApplicationContext applicationContext;
    private final Supplier<T> eventSupplier;

    public EventScheduledService(ApplicationContext applicationContext, Supplier<T> eventSupplier) {
        this.applicationContext = applicationContext;
        this.eventSupplier = eventSupplier;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                applicationContext.publishEvent(eventSupplier.get());
                return null;
            }
        };
    }
}
