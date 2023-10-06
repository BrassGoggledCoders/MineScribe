package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class PackTypeEditorItem extends EditorItem {
    public PackTypeEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return Collections.emptyList();
    }
}
