package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.view.controls.SimpleControl;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class MineScribeSimpleControl<F extends Field<F>> extends SimpleControl<F> {

    protected Label fieldLabel;

    @Override
    public void initializeParts() {
        super.initializeParts();
        this.fieldLabel = new Label();
    }

    @Override
    public void setupBindings() {
        super.setupBindings();

        fieldLabel.textProperty().bind(field.labelProperty());
    }

    protected void layoutForNode(Node node) {
        int columns = field.getSpan();
        Node labelDescription = field.getLabelDescription();
        Node valueDescription = field.getValueDescription();

        add(fieldLabel, 0, 0, 2, 1);
        if (labelDescription != null) {
            GridPane.setValignment(labelDescription, VPos.TOP);
            add(labelDescription, 0, 1, 2, 1);
        }
        add(node, 2, 0, columns - 2, 1);
        if (valueDescription != null) {
            GridPane.setValignment(valueDescription, VPos.TOP);
            add(valueDescription, 2, 1, columns - 2, 1);
        }
    }
}
