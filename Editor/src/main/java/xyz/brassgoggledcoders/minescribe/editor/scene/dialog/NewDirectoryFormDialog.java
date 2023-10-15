package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.editor.validator.StringRegexValidator;

public class NewDirectoryFormDialog extends Dialog<String> {

    private final StringProperty folderName;
    private final Form form;

    public NewDirectoryFormDialog() {
        this.folderName = new SimpleStringProperty();

        this.form = Form.of(Group.of(
                Field.ofStringType("")
                        .label("Folder Name")
                        .bind(folderName)
                        .validate(
                                StringLengthValidator.atLeast(
                                        1,
                                        "Folder Name cannot be empty"
                                ),
                                StringRegexValidator.forRegex(
                                        "^[a-z0-9\\.\\-_]+$",
                                        "Characters must be lower case letters, numbers 0-9, or symbols . - or _"
                                )
                        )
        ));

        FormRenderer formRenderer = new FormRenderer(form);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinWidth(600);
        anchorPane.setMaxHeight(80);
        AnchorPane.setTopAnchor(formRenderer, 0D);
        AnchorPane.setBottomAnchor(formRenderer, 0D);
        AnchorPane.setLeftAnchor(formRenderer, 0D);
        AnchorPane.setRightAnchor(formRenderer, 0D);
        anchorPane.getChildren().add(formRenderer);
        this.getDialogPane().setContent(anchorPane);

        this.getDialogPane().getButtonTypes().add(ButtonTypes.CREATE);
        Button createButton = (Button) this.getDialogPane().lookupButton(ButtonTypes.CREATE);
        createButton.disableProperty().bind(Bindings.not(form.validProperty()));

        this.setResizable(true);
        this.setTitle("Create Namespace Folder");
        this.setResultConverter(this::convertResult);
    }

    private String convertResult(ButtonType buttonType) {
        if (buttonType == ButtonTypes.CREATE) {
            this.form.persist();
            return this.folderName.get();
        } else {
            return null;
        }
    }
}
