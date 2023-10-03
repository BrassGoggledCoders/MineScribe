package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class PackDirectoryEditorItem extends EditorItem {
    public PackDirectoryEditorItem(String name, Path path) {
        super(name, path);
    }

    @Override
    public @Nullable ContextMenu createContextMenu(TreeCell<EditorItem> treeCell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem createNewPack = new MenuItem("Create New Pack");
        contextMenu.setOnAction(event -> {
            Wizard wizard = new Wizard(treeCell, "Create New Pack");
            WizardPane pageOne = new WizardPane();

            GridPane page1Grid = new GridPane();
            page1Grid.setVgap(10);
            page1Grid.setHgap(10);

            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems()
                    .addAll(
                            "Data and Resource Pack",
                            "Data Pack",
                            "Resource Pack"
                    );
            comboBox.setId("packType");
            page1Grid.addRow(0, new Label("Pack Type:"), comboBox);

            TextField field = new TextField();
            field.setId("description");
            page1Grid.addRow(1, new Label("Description:"), field);

            pageOne.setContent(page1Grid);
            wizard.setFlow(new LinearFlow(pageOne));
            wizard.showAndWait();
        });
        contextMenu.getItems().add(createNewPack);
        return contextMenu;
    }
}
