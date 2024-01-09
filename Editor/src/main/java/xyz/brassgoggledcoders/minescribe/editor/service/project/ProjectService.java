package xyz.brassgoggledcoders.minescribe.editor.service.project;

import com.google.inject.Singleton;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;

import java.util.prefs.Preferences;

@Singleton
public class ProjectService implements IProjectService {
    private Project project = null;

    @Override
    public void setCurrentProject(Project project) {
        this.project = project;
        this.saveProject();
    }

    @Override
    public void loadLastProject() {
        Preferences preferences = Preferences.userNodeForPackage(Application.class);
        this.setCurrentProject(Project.tryLoad(preferences));
    }

    @Override
    public void saveProject() {
        if (this.project != null) {
            this.project.trySave(Preferences.userNodeForPackage(Application.class));
        }
    }

    @Override
    public Project get() {
        return this.project;
    }
}
