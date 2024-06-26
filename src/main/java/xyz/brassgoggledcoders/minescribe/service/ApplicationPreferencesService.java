package xyz.brassgoggledcoders.minescribe.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.preferences.ApplicationPreferences;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ApplicationPreferencesService {
    private final ObjectProperty<ApplicationPreferences> applicationPreferences;

    public ApplicationPreferencesService() {
        this.applicationPreferences = new SimpleObjectProperty<>(this, "applicationPreferences", ApplicationPreferences.load());
    }

    @NotNull
    public ApplicationPreferences getApplicationPreferences() {
        return applicationPreferences.get();
    }
}
