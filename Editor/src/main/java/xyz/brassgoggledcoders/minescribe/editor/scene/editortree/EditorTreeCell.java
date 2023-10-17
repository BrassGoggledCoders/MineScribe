package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.util.Objects;

public class EditorTreeCell extends TreeCell<EditorItem> {
    private final Image folderImage = new Image(Objects.requireNonNull(Application.class.getResourceAsStream("icon/folder_16.png")));

    private final ObjectProperty<EventHandler<MouseEvent>> clickHandlerProperty;

    public EditorTreeCell() {
        this.clickHandlerProperty = new SimpleObjectProperty<>();
    }

    @Override
    protected void updateItem(EditorItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            this.setText(item.getName());
            this.setContextMenu(item.createContextMenu(this));
            if (item.isDirectory()) {
                this.setGraphic(new ImageView(folderImage));
            } else {
                ClickHandler clickHandler = new ClickHandler(this);
                this.clickHandlerProperty.set(clickHandler);
                this.setEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            }
        } else {
            this.setText(null);
            this.setContextMenu(null);
            this.setGraphic(null);
            if (this.clickHandlerProperty.get() != null) {
                this.removeEventHandler(MouseEvent.MOUSE_CLICKED, this.clickHandlerProperty.get());
                this.clickHandlerProperty.set(null);
            }

        }
    }

    private record ClickHandler(TreeCell<EditorItem> treeCell) implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if (event.getClickCount() > 1 && event.getButton().equals(MouseButton.PRIMARY)) {
                this.treeCell()
                        .getItem()
                        .onDoubleClick(this.treeCell());
            }
        }
    }
}
