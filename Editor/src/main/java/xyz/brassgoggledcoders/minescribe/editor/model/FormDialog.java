package xyz.brassgoggledcoders.minescribe.editor.model;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import java.util.List;

public class FormDialog<T> extends Dialog<T> {
    public FormDialog() {
        this.setTitle("Create New Pack");
        FormRenderer formRenderer = new FormRenderer(Form.of(Group.of(
                Field.ofSingleSelectionType(List.of("DR", "D", "R"), 0)
                        .label("Pack Type"),
                Field.ofStringType("Name")
                        .label("Name"),
                Field.ofStringType("Description")
                        .label("Description")
        )));
        searchAndSetControlsLabelWidth(formRenderer, 40);
        this.getDialogPane()
                .setContent(formRenderer);

        this.setResizable(true);

        this.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Window wizard = this.getDialogPane().getScene().getWindow();
        wizard.sizeToScene();
    }

    protected void searchAndSetControlsLabelWidth(Pane pane, double labelSize) {
        if (pane instanceof GridPane) {
            if (pane.getStyleClass().stream().anyMatch(s -> s.contains("simple-"))) {
                GridPane gp = (GridPane) pane;
                if (gp.getColumnConstraints().size() == 12) {
                    double rest = 100 - labelSize;
                    for (int i = 0; i < gp.getColumnConstraints().size(); i++) {
                        if (i < 3) {
                            gp.getColumnConstraints().get(i).setPercentWidth(labelSize / 2);
                        } else {
                            gp.getColumnConstraints().get(i).setPercentWidth(rest / 10);
                        }
                    }
                }
            }
        }

        for (Node child : pane.getChildren()) {
            if (child instanceof Pane cpane) {
                searchAndSetControlsLabelWidth(cpane, labelSize);
            }
            else if (child instanceof TitledPane tpane) {
                if (tpane.getContent() instanceof Pane cpane) {
                    searchAndSetControlsLabelWidth(cpane, labelSize);
                }
            }
        }
    }
}
