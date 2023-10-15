package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FormEditorFileEditorItem extends EditorItem {
    private final FileForm fileForm;

    public FormEditorFileEditorItem(String name, Path path, FileForm fileForm) {
        super(name, path);
        this.fileForm = fileForm;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        if (fileForm == null) {
            return false;
        } else {
            return this.getFile().isFile();
        }
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return Collections.emptyList();
    }
}
