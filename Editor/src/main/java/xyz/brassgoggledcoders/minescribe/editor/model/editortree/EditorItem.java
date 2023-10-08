package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class EditorItem {
    private final String name;
    private final Path path;

    public EditorItem(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public File getFile() {
        return this.getPath().toFile();
    }

    @NotNull
    public ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = new ContextMenu();
        if (this.isDirectory()) {
            MenuItem reloadFolder = new MenuItem("Reload Files from Disk");
            reloadFolder.setOnAction(event -> FileHandler.getInstance().reloadDirectory(treeCell.getItem()));
            contextMenu.getItems().add(reloadFolder);
        }

        return contextMenu;
    }


    public boolean isValid() {
        return true;
    }

    public boolean isDirectory() {
        return true;
    }

    @NotNull
    public abstract List<EditorItem> createChildren();

    protected List<File> getChildrenFiles() {
        return this.getChildrenFiles(null);
    }

    protected List<File> getChildrenFiles(FileFilter fileFilter) {
        File[] files = this.getFile().listFiles(fileFilter);

        if (files != null) {
            return Arrays.asList(files);
        } else {
            return Collections.emptyList();
        }
    }
}
