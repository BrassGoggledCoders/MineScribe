package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.controller.tab.FormController;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.OpenTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.NodeTracker;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class FormFileEditorItem extends FileEditorItem {
    private final List<NodeTracker> nodes;

    public FormFileEditorItem(String name, Path path, List<NodeTracker> nodes) {
        super(name, path);
        this.nodes = nodes;
    }

    @Override
    public boolean isValid() {
        File file = this.getFile();
        return file.isFile();
    }

    @Override
    public @NotNull ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = super.createContextMenu(treeCell);
        MenuItem menuItem = new MenuItem("Open File");
        menuItem.setOnAction(event -> openTabFor(treeCell::fireEvent));
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }

    public void openTabFor(Consumer<Event> eventConsumer) {
        this.nodes.stream()
                .filter(node -> node.getForm().isPresent())
                .findFirst()
                .ifPresent(nodeTracker -> eventConsumer.accept(
                        new OpenTabEvent<FormController>(
                                this.getName(),
                                "tab/form",
                                (controller, tabId) -> controller.setFormInfo(
                                        this.getPath(),
                                        nodeTracker.parentType(),
                                        nodeTracker.childTypeOpt()
                                                .orElse(null)
                                ))
                ));
    }

    @Override
    public void onDoubleClick(TreeCell<EditorItem> treeCell) {
        openTabFor(treeCell::fireEvent);
    }
}
