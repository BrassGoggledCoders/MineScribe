package xyz.brassgoggledcoders.minescribe.service;

import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.resource.JsonResourceControl;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class LocalizationService {

    private final SimpleObjectProperty<ResourceBundle> resourceBundle;

    public LocalizationService() {
        resourceBundle = new SimpleObjectProperty<>(
                this,
                "bundle",
                ResourceBundle.getBundle(
                        "minescribe/lang/Language",
                        Locale.US,
                        JsonResourceControl.INSTANCE
                )
        );
    }

    @Nullable
    public ResourceBundle getResourceBundle() {
        return resourceBundle.get();
    }

    public String getString(String name) {
        if (resourceBundle.get() != null) {
            return resourceBundle.get().getString(name);
        } else {
            return "";
        }
    }
}
