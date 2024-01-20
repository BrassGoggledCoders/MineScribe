package xyz.brassgoggledcoders.minescribe.editor.service.editoritem;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;

import java.nio.file.Path;
import java.util.Queue;

public interface IEditorItemService {
    void reloadClosestNode(@NotNull Path path);

    TreeItem<EditorItem> getClosestNode(@NotNull Path path, boolean expand);

    Queue<TreeItem<EditorItem>> getNodePath(Path path);

    void reloadDirectory(@NotNull EditorItem editorItem);

    TreeItem<EditorItem> getRootItem();

    void addPackRepositoryItem(String label, Path location);
}
