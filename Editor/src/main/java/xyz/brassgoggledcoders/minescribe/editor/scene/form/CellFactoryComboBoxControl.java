package xyz.brassgoggledcoders.minescribe.editor.scene.form;

import com.dlsc.formsfx.view.controls.SimpleComboBoxControl;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.function.Function;

public class CellFactoryComboBoxControl<T> extends SimpleComboBoxControl<T> {
    private final Function<T, String> labelCreator;

    public CellFactoryComboBoxControl(Function<T, String> labelCreator) {
        this.labelCreator = labelCreator;
    }

    @Override
    public void initializeParts() {
        super.initializeParts();
        this.comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return object != null ? labelCreator.apply(object) : null;
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
        this.comboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            this.setText(null);
                        } else {
                            this.setText(labelCreator.apply(item));
                        }
                    }
                };
            }
        });
    }
}
