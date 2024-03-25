package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.IPackContentNode;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.PackContentHierarchy;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PackEditorItem extends EditorItem {
    private final IPackContentNode packContentNode;

    public PackEditorItem(String name, Path path) {
        super(name, path);
        this.packContentNode = PackContentHierarchy.getInstance()
                .getNodeFor(RootInfo.PACK);
    }

    @Override
    public boolean isValid() {
        File file = this.getFile();
        File[] files = file.listFiles(File::isDirectory);
        return files != null && files.length > 0 && new File(file, "pack.mcmeta").exists();
    }

    @Override
    @NotNull
    public List<EditorItem> createChildren(DirectoryStream<Path> childPaths) {
        List<EditorItem> childrenEditorItems = new ArrayList<>();
        for (Path childPath : childPaths) {
            Optional<MineScribePackType> packTypeOpt = Optional.empty();
            if (Files.isDirectory(childPath)) {
                packTypeOpt = Registries.getPackTypeRegistry()
                        .getValues()
                        .stream()
                        .filter(type -> childPath.endsWith(type.folder()))
                        .findFirst();

                packTypeOpt.ifPresent(packType -> childrenEditorItems.add(new PackTypeEditorItem(
                        childPath.getFileName()
                                .toString(),
                        childPath,
                        packType
                )));
            }
            if (packTypeOpt.isEmpty()) {
                Path relativePath = this.getPath().relativize(childPath);
                IPackContentNode childNode = this.packContentNode.getNode(relativePath);
                if (childNode != null) {
                    if (Files.isDirectory(childPath)) {
                        childrenEditorItems.add(new PackContentTypeEditorItem(childPath.getFileName().toString(), childPath, childNode));
                    } else if (Files.isRegularFile(childPath)) {
                        childrenEditorItems.add(new FormFileEditorItem(
                                relativePath.toString(),
                                childPath,
                                childNode.getNodeTrackers()
                        ));
                    }
                }
            }
        }

        return childrenEditorItems;
    }
}
