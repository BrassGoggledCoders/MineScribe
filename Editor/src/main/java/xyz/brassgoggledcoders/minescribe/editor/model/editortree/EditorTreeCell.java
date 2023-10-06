package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.util.Objects;

public class EditorTreeCell extends TreeCell<EditorItem> {
    private final Image folderImage = new Image(Objects.requireNonNull(Application.class.getResourceAsStream("icon/folder_16.png")));

    public EditorTreeCell() {

    }

    @Override
    protected void updateItem(EditorItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            this.setText(item.getName());
            this.setContextMenu(item.createContextMenu(this));
            if (item.isDirectory()) {
                this.setGraphic(new ImageView(folderImage));
            }
        } else {
            this.setText(null);
            this.setContextMenu(null);
            this.setGraphic(null);
        }
    }
}
