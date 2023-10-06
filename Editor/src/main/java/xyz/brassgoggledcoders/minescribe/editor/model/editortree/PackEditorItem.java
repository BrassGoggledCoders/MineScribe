package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        File[] childrenFiles = this.getPath()
                .toFile()
                .listFiles();

        List<EditorItem> childrenEditorItems = new ArrayList<>();
        if (childrenFiles != null) {
            for (File childFile : childrenFiles) {
                if (childFile.isDirectory()) {
                    childrenEditorItems.add(new PackTypeEditorItem(childFile.getName(), childFile.toPath()));
                } else if (childFile.getName().equalsIgnoreCase("pack.mcmeta")) {
                    childrenEditorItems.add(new FileEditorItem(childFile.getName(), childFile.toPath()));
                }
            }
        }
        childrenEditorItems.removeIf(Predicate.not(EditorItem::isValid));

        return childrenEditorItems;
    }
}
