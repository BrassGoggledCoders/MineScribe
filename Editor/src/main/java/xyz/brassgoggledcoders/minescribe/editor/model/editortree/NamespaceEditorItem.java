package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.editor.registry.PackContentTypeRegistry;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NamespaceEditorItem extends EditorItem {
    private final MineScribePackType packType;
    private final Collection<PackContentType> validTypes;

    public NamespaceEditorItem(String name, Path path, MineScribePackType packType) {
        super(name, path);
        this.packType = packType;
        this.validTypes = PackContentTypeRegistry.getInstance()
                .getPackContentTypesFor(this.packType);
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        List<File> childrenFiles = this.getChildrenFiles();
        List<EditorItem> editorItems = new ArrayList<>();
        for (File childFile : childrenFiles) {
            if (childFile.isDirectory()) {
                Path childPath = childFile.toPath();
                List<PackContentType> typesForFile = validTypes.parallelStream()
                        .filter(packContentType -> childPath.endsWith(packContentType.getPath().getName(0)))
                        .toList();

                if (!typesForFile.isEmpty()) {
                    editorItems.add(new PackContentTypeEditorItem(childFile.getName(), childPath, typesForFile));
                }
            }
        }
        return editorItems;
    }
}
