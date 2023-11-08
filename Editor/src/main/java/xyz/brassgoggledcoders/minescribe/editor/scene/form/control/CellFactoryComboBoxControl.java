package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import com.dlsc.formsfx.view.controls.SimpleComboBoxControl;
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
        this.comboBox.setConverter(new LabeledCellConverter<>(this.labelCreator));
        this.comboBox.setCellFactory(new LabeledCellFactory<>(this.labelCreator));
    }
}
