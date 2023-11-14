package xyz.brassgoggledcoders.minescribe.editor.scene.table;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.function.Function;

public class ImageTableCell<S, T> extends TableCell<S, T> {
    private final Function<T, Image> imageFunction;
    private final ImageView imageView;

    public ImageTableCell(Function<T, Image> imageFunction) {
        this.imageFunction = imageFunction;
        this.imageView = new ImageView();
        this.setGraphic(imageView);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            this.imageView.setImage(this.imageFunction.apply(item));
        } else {
            this.imageView.setImage(null);
        }
    }
}
