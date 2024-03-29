package xyz.brassgoggledcoders.minescribe.editor.scene.table;

import javafx.scene.control.TableCell;
import org.kordamp.ikonli.javafx.FontIcon;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;

public class MessageTypeCell<S> extends TableCell<S, MessageType> {
    private final FontIcon fontIcon;

    public MessageTypeCell() {
        this.fontIcon = new FontIcon();
        this.setGraphic(this.fontIcon);
    }

    @Override
    protected void updateItem(MessageType item, boolean empty) {
        super.updateItem(item, empty);
        for (MessageType type : MessageType.values()) {
            if (type == item) {
                this.fontIcon.getStyleClass()
                        .add(type.getStyle());
            } else {
                this.fontIcon.getStyleClass()
                        .remove(type.getStyle());
            }
        }
        if (item != null) {
            this.setGraphic(this.fontIcon);
            this.fontIcon.setIconCode(item.getFeather());
        } else {
            this.setGraphic(null);
        }
    }
}
