package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.function.Function;

public class LabeledCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {
    private final Function<T, FancyText> labelMaker;

    public LabeledCellFactory(Function<T, FancyText> labelMaker) {
        this.labelMaker = labelMaker;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    this.setText("");
                } else {
                    this.setText(LabeledCellFactory.this.labelMaker.apply(item).getText());
                }
            }
        };
    }
}
