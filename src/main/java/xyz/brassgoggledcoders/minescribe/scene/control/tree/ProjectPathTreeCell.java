package xyz.brassgoggledcoders.minescribe.scene.control.tree;

import javafx.scene.control.TreeCell;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import java.nio.file.Files;

public class ProjectPathTreeCell extends TreeCell<ProjectPathValue> {

    @Override
    protected void updateItem(ProjectPathValue item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            this.setText(item.getName());
            if (Files.isDirectory(item.getPath())) {
                this.setGraphic(new FontIcon(MaterialDesignF.FOLDER_OUTLINE));
            } else {
                this.setGraphic(new FontIcon(MaterialDesignF.FILE_OUTLINE));
            }
        } else {
            this.setText(null);
            this.setContextMenu(null);
            this.setGraphic(null);
        }
    }
}
