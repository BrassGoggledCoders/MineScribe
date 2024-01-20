package xyz.brassgoggledcoders.minescribe.editor.service.project;

import com.google.inject.Provider;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;

public interface IProjectService extends Provider<Project> {
    void setCurrentProject(Project project);

    void loadLastProject();

    void saveProject();
}
