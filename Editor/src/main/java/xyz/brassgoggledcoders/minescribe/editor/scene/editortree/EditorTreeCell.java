package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class EditorTreeCell extends TreeCell<EditorItem> {
    private final ObjectProperty<EventHandler<MouseEvent>> clickHandlerProperty;

    public EditorTreeCell() {
        this.clickHandlerProperty = new SimpleObjectProperty<>();
    }

    @Override
    protected void updateItem(EditorItem item, boolean empty) {
        EditorItem previousItem = this.getItem();
        if (previousItem != null) {
            if (previousItem.getCssClass() != null) {
                this.getStyleClass()
                        .remove(previousItem.getCssClass());
            }
        }
        super.updateItem(item, empty);
        if (item != null) {
            this.setText(item.getName());
            this.setContextMenu(item.createContextMenu(this));
            if (item.isDirectory()) {
                this.setGraphic(new FontIcon(Feather.FOLDER));
            } else {
                this.setGraphic(new FontIcon(Feather.FILE));
                ClickHandler clickHandler = new ClickHandler(this);
                this.clickHandlerProperty.set(clickHandler);
                this.setEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            }
            if (item.getCssClass() != null) {
                this.getStyleClass()
                        .add(item.getCssClass());
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
