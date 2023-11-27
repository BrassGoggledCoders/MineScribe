package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.NodeTracker;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.ZeroPaddedFormRenderer;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.CellFactoryComboBoxControl;
import xyz.brassgoggledcoders.minescribe.editor.validation.StringRegexValidator;

import java.util.List;
import java.util.Optional;

public class NewFileFormDialog extends Dialog<NewFileFormDialog.NewFileResult> {
    private final ObjectProperty<PackContentParentType> parentType;
    private final ObjectProperty<PackContentChildType> childType;
    private final FilteredList<PackContentChildType> childTypesFiltered;
    private final StringProperty fileName;
    private final Form form;

    public NewFileFormDialog(@NotNull List<NodeTracker> nodeTrackers) {
        this.childType = new SimpleObjectProperty<>();
        this.fileName = new SimpleStringProperty("");

        List<PackContentParentType> parentTypes = nodeTrackers.stream()
                .map(NodeTracker::parentType)
                .distinct()
                .toList();

        List<PackContentChildType> childTypes = nodeTrackers.stream()
                .flatMap(nodeTracker -> nodeTracker.childTypeOpt().stream())
                .distinct()
                .toList();

        if (parentTypes.isEmpty()) {
            parentTypes = EditorRegistries.getContentParentTypes()
                    .getValues();
        }
        if (childTypes.isEmpty()) {
            childTypes = EditorRegistries.getContentChildTypes()
                    .getValues();
        }

        this.childTypesFiltered = FXCollections.observableArrayList(childTypes)
                .filtered(null);

        SingleSelectionField<PackContentParentType> parentField = Field.ofSingleSelectionType(parentTypes)
                .label("Parent Type")
                .render(() -> new CellFactoryComboBoxControl<>(PackContentType::getLabel))
                .required("Parent Type is required");

        this.parentType = parentField.selectionProperty();


        SingleSelectionField<PackContentChildType> childField = Field.ofSingleSelectionType(
                        new SimpleListProperty<>(this.childTypesFiltered),
                        this.childType
                )
                .render(() -> new CellFactoryComboBoxControl<>(PackContentType::getLabel))
                .label("Child Type")
                .required("Child Type is required");


        this.parentType.addListener(((observable, oldValue, newValue) -> {
            ResourceId parentId = EditorRegistries.getContentParentTypes()
                    .getKey(newValue);
            this.childTypesFiltered.setPredicate(
                    childValue -> childValue.getParentId().equals(parentId)
            );
            childField.required(!this.childTypesFiltered.isEmpty());
            childField.editable(!this.childTypesFiltered.isEmpty());
        }));

        childField.required(!this.childTypesFiltered.isEmpty());
        childField.editable(!this.childTypesFiltered.isEmpty());

        if (parentTypes.size() == 1) {
            this.parentType.set(parentTypes.get(0));

            if (childTypes.size() == 1) {
                this.childType.set(childTypes.get(0));
            }
        }

        this.form = Form.of(Group.of(
                parentField,
                childField,
                Field.ofStringType(this.fileName)
                        .label("File Name")
                        .required(true)
                        .validate(StringRegexValidator.forRegex(
                                "^[a-z0-9/\\.\\-_]+$",
                                "Characters must be lower case letters, numbers 0-9, or symbols . - / or _"
                        ))
        ));

        FormRenderer formRenderer = new ZeroPaddedFormRenderer(form);
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
        this.setTitle("Create New File");
        this.setResultConverter(this::convertResult);
    }

    private NewFileResult convertResult(ButtonType buttonType) {
        if (buttonType == ButtonTypes.CREATE) {
            this.form.persist();
            return new NewFileResult(
                    this.parentType.get(),
                    Optional.ofNullable(this.childType.getValue()),
                    this.fileName.get()
            );
        } else {
            return null;
        }
    }

    public record NewFileResult(
            PackContentParentType parentType,
            Optional<PackContentChildType> childTypeOpt,
            String fileName
    ) {

    }
}
