package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class UnsavedFileEditorItem extends FileEditorItem {
    private final UUID tabId;

    public UnsavedFileEditorItem(String name, Path path, UUID tabID) {
        super(name, path);
        this.tabId = tabID;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public boolean isValid() {
        Project project = InfoRepository.getInstance()
                .getValue(Project.KEY);

        if (project != null) {
            boolean openTab = Optional.ofNullable(project.getOpenTabs()
                            .get(tabId)
                    )
                    .filter(this.getPath()::equals)
                    .isPresent();
            if (openTab) {
                return !Files.exists(this.getPath());
            }
        }

        return false;
    }

    @Override
    public String getCssClass() {
        return "editor-item-unsaved";
    }
}
