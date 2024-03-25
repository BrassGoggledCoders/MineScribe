package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.collections.FXCollections;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.NodeTracker;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.EditorFormTab;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.FileTab;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
        menuItem.setOnAction(event -> openTab());
        contextMenu.getItems().add(0, menuItem);
        return contextMenu;
    }

    public void openTab() {
        Optional<NodeTracker> nodeTrackerOpt = this.nodes.stream()
                .filter(node -> node.getForm().isPresent())
                .findFirst();

        if (nodeTrackerOpt.isPresent()) {
            nodeTrackerOpt.ifPresent(nodeTracker -> {
                EditorFormTab editorFormTab = this.getEditorTabService()
                        .openTab("form", this.getPath());

                if (editorFormTab != null) {
                    editorFormTab.setText(this.getName());
                    nodeTracker.getForm()
                            .ifPresent(editorFormTab.fileFormProperty()::set);
                    editorFormTab.parentsProperty()
                            .setValue(FXCollections.observableList(nodeTracker.getFullNames()));
                }
            });
        } else {
            FileTab fileTab = this.getEditorTabService()
                    .openTab("file_view", this.getPath());

            if (fileTab != null) {
                fileTab.setText(this.getName());
                fileTab.pathProperty()
                        .setValue(this.getPath());
            }
        }

    }

    @Override
    public void onDoubleClick(TreeCell<EditorItem> treeCell) {
        openTab();
    }
}
