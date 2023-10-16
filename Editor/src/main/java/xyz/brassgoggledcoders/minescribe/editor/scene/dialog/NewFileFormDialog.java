package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype.NodeTracker;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.CellFactoryComboBoxControl;
import xyz.brassgoggledcoders.minescribe.editor.validator.StringRegexValidator;

public class NewFileFormDialog extends Dialog<String> {
    private final ObjectProperty<PackContentParentType> parentType;
    private final ObjectProperty<PackContentChildType> childType;
    private final FilteredList<PackContentChildType> childTypes;
    private final StringProperty fileName;
    private final Form form;

    public NewFileFormDialog(@Nullable NodeTracker nodeTracker) {
        this.childType = new SimpleObjectProperty<>();
        this.fileName = new SimpleStringProperty("");

        SingleSelectionField<PackContentParentType> parentField = Field.ofSingleSelectionType(Registries.getContentParentTypes()
                        .getValues()
                )
                .label("Parent Type:")
                .render(() -> new CellFactoryComboBoxControl<>(PackContentType::getLabel))
                .required(true);

        this.parentType = parentField.selectionProperty();

        this.childTypes = FXCollections.observableArrayList(Registries.getContentChildTypes().getValues())
                .filtered(null);
        this.parentType.addListener(((observable, oldValue, newValue) -> this.childTypes.setPredicate(
                childValue -> childValue.getParentId().equals(newValue.getId())
        )));

        SingleSelectionField<PackContentChildType> childField = Field.ofSingleSelectionType(
                        new SimpleListProperty<>(this.childTypes),
                        this.childType
                )
                .render(() -> new CellFactoryComboBoxControl<>(PackContentType::getLabel))
                .label("Child Type");

        childField.itemsProperty().addListener((ListChangeListener<PackContentChildType>) c -> {
            childField.requiredProperty().set(!c.getList().isEmpty());
        });

        if (nodeTracker != null) {
            this.parentType.set(nodeTracker.parentType());
            nodeTracker.childTypeOpt().ifPresent(this.childType::set);
        }

        this.form = Form.of(Group.of(
                parentField,
                childField,
                Field.ofStringType(this.fileName)
                        .label("File Name")
                        .required(true)
                        .validate(StringRegexValidator.forRegex(
                                "^[a-z0-9\\.\\-_]+$",
                                "Characters must be lower case letters, numbers 0-9, or symbols . - or _"
                        ))
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
            return this.fileName.get();
        } else {
            return null;
        }
    }
}
