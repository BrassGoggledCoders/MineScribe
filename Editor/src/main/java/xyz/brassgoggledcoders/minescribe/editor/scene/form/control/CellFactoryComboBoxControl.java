package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

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
    public void setupBindings() {
        super.setupBindings();
        this.readOnlyLabel.textProperty().unbind();
        this.readOnlyLabel.textProperty().set("");
        this.readOnlyLabel.visibleProperty().unbind();
        this.comboBox.visibleProperty().unbind();
        this.comboBox.disableProperty().bind(this.field.editableProperty().not());
    }

    @Override
    public void initializeParts() {
        super.initializeParts();
        this.comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object != null ? labelCreator.apply(object) : "";
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
                    {
                        this.setText("");
                    }

                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            this.setText("");
                        } else {
                            this.setText(labelCreator.apply(item));
                        }
                    }
                };
            }
        });
    }
}
