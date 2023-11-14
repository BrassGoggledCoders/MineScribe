package xyz.brassgoggledcoders.minescribe.editor.scene.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;

public class MessageTypeCell<S> extends TableCell<S, MessageType> {
    private final ImageView imageView;

    public MessageTypeCell() {
        this.imageView = new ImageView();
        this.setGraphic(imageView);
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
            this.imageView.setImage(item.getImage());

        } else {
            this.imageView.setImage(null);
        }
    }
}
