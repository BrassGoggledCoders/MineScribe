package xyz.brassgoggledcoders.minescribe.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public record Project(
        Path projectPath
) {
    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Project loadProject(Preferences preferences) {
        try {
            String project = preferences.get("project", "");
            if (!project.isEmpty()) {
                return MAPPER.readValue(project, Project.class);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to load project info", e);
        }

        return null;
    }

    public static void saveProject(Preferences preferences, Project newProject) {
        try {
            preferences.put("project", MAPPER.writeValueAsString(newProject));
            preferences.flush();
        } catch (BackingStoreException | JsonProcessingException e) {
            LOGGER.error("Failed to save project info", e);
        }
    }
}
