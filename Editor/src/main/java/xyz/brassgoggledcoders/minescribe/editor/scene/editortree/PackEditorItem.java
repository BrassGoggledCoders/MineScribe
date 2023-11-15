package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.io.File;
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
    public List<EditorItem> createChildren() {
        List<File> childrenFiles = this.getChildrenFiles();
        List<EditorItem> childrenEditorItems = new ArrayList<>();
        for (File childFile : childrenFiles) {
            if (childFile.isDirectory()) {
                Path childPath = childFile.toPath();
                Registries.getPackTypes()
                        .getValues()
                        .stream()
                        .filter(type -> childPath.endsWith(type.folder()))
                        .findFirst()
                        .ifPresent(packType -> childrenEditorItems.add(new PackTypeEditorItem(
                                childFile.getName(),
                                childPath,
                                packType
                        )));
            } else if (childFile.getName().equalsIgnoreCase("pack.mcmeta")) {
                childrenEditorItems.add(new NoFormFileEditorItem(childFile.getName(), childFile.toPath()));
            }
        }

        return childrenEditorItems;
    }
}
