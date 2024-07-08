package xyz.brassgoggledcoders.minescribe.event;


import org.springframework.context.ApplicationEvent;
import xyz.brassgoggledcoders.minescribe.project.Project;

import java.nio.file.Path;

public class ProjectOpenedEvent extends ApplicationEvent {
    private final Project project;
    private final Path path;

    public ProjectOpenedEvent(Project project, Path path) {
        super(project);
        this.project = project;
        this.path = path;
    }

    public Project getProject() {
        return project;
    }

    public Path getPath() {
        return path;
    }
}
