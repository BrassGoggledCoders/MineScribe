package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import org.jetbrains.annotations.NotNull;

import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileEditorItem extends EditorItem {
    public FileEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public @NotNull List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        return Collections.emptyList();
    }
}
