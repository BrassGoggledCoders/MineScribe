package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PackContentSubTypeEditorItem extends EditorItem {
    private final Map<PackContentType, List<PackContentType>> packContentSubTypes;
    public PackContentSubTypeEditorItem(String name, Path path, Map<PackContentType, List<PackContentType>> packContentSubTypes) {
        super(name, path);
        this.packContentSubTypes = packContentSubTypes;
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        return Collections.emptyList();
    }
}
