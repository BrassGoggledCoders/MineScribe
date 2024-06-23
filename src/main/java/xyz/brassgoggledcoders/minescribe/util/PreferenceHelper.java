package xyz.brassgoggledcoders.minescribe.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.MineScribe;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferenceHelper {
    private static final Preferences APPLICATION_PREFERENCES = Preferences.userNodeForPackage(MineScribe.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceHelper.class);

    public static <T> T loadPreference(Class<T> clazz, String key) {
        try {
            String project = APPLICATION_PREFERENCES.get(key, "");
            if (!project.isEmpty()) {
                return MAPPER.readValue(project, clazz);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to load preferences from {}", key, e);
        }

        return null;
    }

    public static <T> void savePreferences(T value, String key) {
        try {
            APPLICATION_PREFERENCES.put(key, MAPPER.writeValueAsString(value));
            APPLICATION_PREFERENCES.flush();
        } catch (BackingStoreException | JsonProcessingException e) {
            LOGGER.error("Failed to save preferences for {}", key, e);
        }
    }
}
