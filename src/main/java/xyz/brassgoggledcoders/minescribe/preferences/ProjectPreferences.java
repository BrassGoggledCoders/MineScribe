package xyz.brassgoggledcoders.minescribe.preferences;

import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.util.PreferenceHelper;

import java.util.UUID;

public class ProjectPreferences {
    private final UUID projectUUID;

    public ProjectPreferences(UUID projectUUID) {
        this.projectUUID = projectUUID;
    }

    public void save() {
        PreferenceHelper.savePreferences(this, "project.%s".formatted(this.projectUUID));
    }

    public static ProjectPreferences load(Project project) {
        return PreferenceHelper.loadOrCreate(ProjectPreferences.class, "project.%s".formatted(project.uuid()), () -> new ProjectPreferences(project.uuid()));
    }
}
