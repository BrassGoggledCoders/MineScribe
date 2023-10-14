package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.IPackContentNode;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackContentTypeEditorItem extends EditorItem {
    private final IPackContentNode packContentNode;

    public PackContentTypeEditorItem(String name, Path path, IPackContentNode packContentNode) {
        super(name, path);
        this.packContentNode = packContentNode;
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        List<File> childrenFiles = this.getChildrenFiles();
        List<EditorItem> editorItems = new ArrayList<>();
        for (File childFile : childrenFiles) {

        }

        return editorItems;
    }
}
