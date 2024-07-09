package xyz.brassgoggledcoders.minescribe.controller.toolwindow;

import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.collection.namedtree.NamedTree;
import xyz.brassgoggledcoders.minescribe.model.pack.Pack;
import xyz.brassgoggledcoders.minescribe.scene.control.tree.*;
import xyz.brassgoggledcoders.minescribe.service.PackService;
import xyz.brassgoggledcoders.minescribe.service.file.DirectoryWatchService;

import java.nio.file.Path;

@Component
public class ProjectController {
    private final PackService packService;
    private final DirectoryWatchService directoryWatchService;

    @FXML
    private TreeView<ProjectPathValue> projectFileView;

    public ProjectController(PackService packService, DirectoryWatchService directoryWatchService) {
        this.packService = packService;
        this.directoryWatchService = directoryWatchService;
    }

    @FXML
    private void initialize() {
        this.projectFileView.setCellFactory(treeView -> new ProjectPathTreeCell());

        for (Pack importedPack : this.packService.getImportedPacks()) {
            addImportedPack(importedPack);
        }

        this.packService.getImportedPacks()
                .addListener(this::handleChanges);
    }

    private void addImportedPack(Pack importedPack) {
        NamedTree<String, Path> namedTree = this.directoryWatchService.getWatchedPath(importedPack.path());
        this.projectFileView.getRoot()
                .getChildren()
                .add(new NamedTreeItem<>(
                        new PackProjectPathValue(importedPack),
                        namedTree,
                        (projectPathValue, namedTreeValue) -> new DirectoryProjectPathValue(
                                namedTreeValue.getKey(),
                                namedTreeValue.getValue()
                        )
                ));
    }

    private void handleChanges(Change<? extends Pack> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (Pack pack : change.getAddedSubList()) {
                    this.addImportedPack(pack);
                }
            } else if (change.wasRemoved()) {
                for (Pack pack : change.getRemoved()) {
                    this.projectFileView.getRoot()
                            .getChildren()
                            .removeIf(treeItem -> {
                                if (treeItem.getValue() instanceof PackProjectPathValue packProjectPathValue) {
                                    return packProjectPathValue.getPack()
                                            .equals(pack);
                                }
                                return false;
                            });
                }
            }
        }
    }
}
