package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import xyz.brassgoggledcoders.minescribe.editor.project.Project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

public class UnsavedFileEditorItem extends FileEditorItem {
    private final Supplier<Project> projectSupplier;

    public UnsavedFileEditorItem(String name, Path path, Supplier<Project> projectSupplier) {
        super(name, path);
        this.projectSupplier = projectSupplier;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public boolean isValid() {
        Project project = this.projectSupplier.get();

        if (project != null) {
            if (project.getOpenTabs().contains(this.getPath())) {
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
