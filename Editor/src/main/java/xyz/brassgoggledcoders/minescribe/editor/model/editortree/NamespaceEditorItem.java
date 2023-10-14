package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.PackContentHierarchy;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NamespaceEditorItem extends EditorItem {
    private final IPackContentNode contentNode;

    public NamespaceEditorItem(String name, Path path, MineScribePackType packType) {
        super(name, path);
        this.contentNode = PackContentHierarchy.getInstance()
                .getNodeFor(packType);
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        List<File> childrenFiles = this.getChildrenFiles();
        List<EditorItem> editorItems = new ArrayList<>();
        for (File childFile : childrenFiles) {
            if (childFile.isDirectory()) {
                Path childPath = this.getPath().relativize(childFile.toPath());
                IPackContentNode packContentNode = contentNode.getNode(childPath);

                if (packContentNode != null) {
                    editorItems.add(new PackContentTypeEditorItem(childFile.getName(), childPath, packContentNode));
                }
            }
        }
        return editorItems;
    }
}
