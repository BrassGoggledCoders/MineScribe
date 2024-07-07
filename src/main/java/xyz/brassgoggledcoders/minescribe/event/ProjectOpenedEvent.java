package xyz.brassgoggledcoders.minescribe.event;


import org.springframework.context.ApplicationEvent;
import xyz.brassgoggledcoders.minescribe.project.Project;

public class ProjectOpenedEvent extends ApplicationEvent {
    private final Project project;

    public ProjectOpenedEvent(Project project) {
        super(project);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
