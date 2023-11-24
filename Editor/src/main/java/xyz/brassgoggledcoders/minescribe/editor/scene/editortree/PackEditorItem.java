package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackEditorItem extends EditorItem {
    public PackEditorItem(String name, Path path) {
        super(name, path);
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
            if (Files.isDirectory(childPath)) {
                Registries.getPackTypeRegistry()
                        .getValues()
                        .stream()
                        .filter(type -> childPath.endsWith(type.folder()))
                        .findFirst()
                        .ifPresent(packType -> childrenEditorItems.add(new PackTypeEditorItem(
                                childPath.getFileName()
                                        .toString(),
                                childPath,
                                packType
                        )));
            } else if (childPath.getFileName().startsWith("pack.mcmeta")) {
                childrenEditorItems.add(new NoFormFileEditorItem(childPath.getFileName().toString(), childPath));
            }
        }

        return childrenEditorItems;
    }
}
