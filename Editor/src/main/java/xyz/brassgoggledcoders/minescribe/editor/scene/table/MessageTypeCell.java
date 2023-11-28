package xyz.brassgoggledcoders.minescribe.editor.scene.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.paint.Color;
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
        TableRow<S> tableRow = getTableRow();
        for (MessageType type : MessageType.values()) {
            if (type == item) {
                tableRow.getStyleClass()
                        .add("table-row-" + type.getName());
            } else {
                tableRow.getStyleClass()
                        .remove("table-row-" + type.getName());
            }
        }
        if (item != null) {
            this.setGraphic(this.fontIcon);
            this.fontIcon.setIconCode(item.getFeather());
            this.fontIcon.setIconColor(Color.RED);
        } else {
            this.setGraphic(null);
        }
    }
}
