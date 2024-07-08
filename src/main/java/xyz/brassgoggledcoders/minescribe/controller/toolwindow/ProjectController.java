package xyz.brassgoggledcoders.minescribe.controller.toolwindow;

import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.model.pack.Pack;
import xyz.brassgoggledcoders.minescribe.scene.control.tree.PackProjectPathValue;
import xyz.brassgoggledcoders.minescribe.scene.control.tree.ProjectPathTreeCell;
import xyz.brassgoggledcoders.minescribe.scene.control.tree.ProjectPathValue;
import xyz.brassgoggledcoders.minescribe.service.PackService;

@Component
public class ProjectController {
    private final PackService packService;

    @FXML
    private TreeView<ProjectPathValue> projectFileView;

    public ProjectController(PackService packService) {
        this.packService = packService;
    }

    @FXML
    private void initialize() {
        this.projectFileView.setCellFactory(treeView -> new ProjectPathTreeCell());

        this.packService.getImportedPacks()
                .forEach(pack -> projectFileView.getRoot()
                        .getChildren()
                        .add(new TreeItem<>(new PackProjectPathValue(pack)))
                );

        this.packService.getImportedPacks()
                .addListener(this::handleChanges);
    }

    private void handleChanges(Change<? extends Pack> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (Pack pack : change.getAddedSubList()) {
                    this.projectFileView.getRoot()
                            .getChildren()
                            .add(new TreeItem<>(new PackProjectPathValue(pack)));
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
