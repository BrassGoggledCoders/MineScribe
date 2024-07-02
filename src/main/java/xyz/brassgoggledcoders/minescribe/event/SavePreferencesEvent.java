package xyz.brassgoggledcoders.minescribe.event;

import org.springframework.context.ApplicationEvent;

public class SavePreferencesEvent extends ApplicationEvent {
    public SavePreferencesEvent(Object source) {
        super(source);
    }
}
