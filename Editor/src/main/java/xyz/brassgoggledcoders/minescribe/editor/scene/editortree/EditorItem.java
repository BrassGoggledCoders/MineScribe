package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.scene.dialog.ExceptionDialog;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public abstract class EditorItem implements Comparable<EditorItem> {
    private final Logger LOGGER = LoggerFactory.getLogger(EditorItem.class);

    private final String name;
    private final Path path;

    public EditorItem(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    /**
     * @return if the file is automatically created from a parent
     */
    public boolean isAutomatic() {
        return true;
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
        MenuItem deleteItem = new MenuItem("Delete %s".formatted(this.isDirectory() ? "Directory" : "File"));
        deleteItem.setOnAction(event -> {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete %s".formatted(this.getPath()),
                    ButtonType.NO, ButtonType.YES
            );
            alert.showAndWait()
                    .filter(buttonType -> buttonType == ButtonType.YES)
                    .ifPresent(ignored -> {
                        try {
                            Files.deleteIfExists(this.getPath());
                            FileHandler.getInstance()
                                    .reloadClosestNode(this.getPath().getParent());
                        } catch (IOException e) {
                            LOGGER.error("Failed to delete path: {}", this.getPath(), e);
                            ExceptionDialog.showDialog(
                                    "Failed to delete Path: %s".formatted(this.getPath()),
                                    e
                            );
                        }
                    });
        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }


    public boolean isValid() {
        return true;
    }

    public boolean isDirectory() {
        return true;
    }

    public void onDoubleClick(TreeCell<EditorItem> treeCell) {

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

    protected <T> List<T> runForChildren(Function<Path, Optional<T>> runOnPath) {
        try (DirectoryStream<Path> childrenPaths = Files.newDirectoryStream(this.getPath())) {
            List<T> values = new ArrayList<>();
            for (Path childPath : childrenPaths) {
                runOnPath.apply(childPath)
                        .ifPresent(values::add);
            }
            return values;
        } catch (IOException e) {
            LOGGER.error("Failed to open Directory Stream for {}", this.getPath(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public int compareTo(@NotNull EditorItem o) {
        if (this.isDirectory() && !o.isDirectory()) {
            return -1;
        } else if (!this.isDirectory() && o.isDirectory()) {
            return 1;
        } else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), o.getName());
        }
    }

    public String getCssClass() {
        return null;
    }
}
