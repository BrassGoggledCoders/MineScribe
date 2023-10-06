package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileEditorItem extends EditorItem {
    public FileEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public boolean isValid() {
        File file = this.getFile();
        return file.isFile() && file.exists();
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return Collections.emptyList();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
