package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import javafx.scene.control.TreeCell;

public class EditorTreeCell extends TreeCell<EditorItem> {
    public EditorTreeCell() {

    }

    @Override
    protected void updateItem(EditorItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            this.setText(item.getName());
            this.setContextMenu(item.createContextMenu(this));
        } else {
            this.setText(null);
            this.setContextMenu(null);
        }
    }
}
