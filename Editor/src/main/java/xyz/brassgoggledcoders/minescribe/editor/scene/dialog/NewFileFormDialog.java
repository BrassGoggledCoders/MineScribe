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
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.Holder;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy.NodeTracker;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.ZeroPaddedFormRenderer;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.CellFactoryComboBoxControl;
import xyz.brassgoggledcoders.minescribe.editor.validation.StringRegexValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NewFileFormDialog extends Dialog<NewFileFormDialog.NewFileResult> {
    private final ObjectProperty<Holder<ResourceId, PackContentParentType>> parentType;
    private final ObjectProperty<Holder<ResourceId, PackContentChildType>> childType;
    private final FilteredList<Holder<ResourceId, PackContentChildType>> childTypesFiltered;
    private final StringProperty fileName;
    private final Form form;

    public NewFileFormDialog(@NotNull List<NodeTracker> nodeTrackers) {
        this.childType = new SimpleObjectProperty<>();
        this.fileName = new SimpleStringProperty("");

        List<Holder<ResourceId, PackContentParentType>> parentTypes = nodeTrackers.stream()
                .map(NodeTracker::parentTypeHolder)
                .distinct()
                .toList();

        List<Holder<ResourceId, PackContentChildType>> childTypes = nodeTrackers.stream()
                .flatMap(nodeTracker -> nodeTracker.childTypeHolderOpt()
                        .stream()
                )
                .distinct()
                .toList();

        if (parentTypes.isEmpty()) {
            parentTypes = new ArrayList<>(EditorRegistries.getContentParentTypes()
                    .getHolders()
            );
        }
        if (childTypes.isEmpty()) {
            childTypes = new ArrayList<>(EditorRegistries.getContentChildTypes()
                    .getHolders()
            );
        }

        this.childTypesFiltered = FXCollections.observableArrayList(childTypes)
                .filtered(null);

        SingleSelectionField<Holder<ResourceId, PackContentParentType>> parentField = Field.ofSingleSelectionType(parentTypes)
                .label("Parent Type")
                .render(() -> new CellFactoryComboBoxControl<>(holder -> holder.fold(
                        PackContentType::getLabel,
                        () -> FancyText.literal("<Missing Value>")
                )))
                .required("Parent Type is required");

        this.parentType = parentField.selectionProperty();


        SingleSelectionField<Holder<ResourceId, PackContentChildType>> childField = Field.ofSingleSelectionType(
                        new SimpleListProperty<>(this.childTypesFiltered),
                        this.childType
                )
                .render(() -> new CellFactoryComboBoxControl<>(holder -> holder.fold(
                        PackContentType::getLabel,
                        () -> FancyText.literal("<Missing Value>")
                )))
                .label("Child Type")
                .required("Child Type is required");


        this.parentType.addListener(((observable, oldValue, newValue) -> {
            this.childTypesFiltered.setPredicate(
                    childValue -> childValue.exists(value -> value.getParentId().equals(newValue.getKey()))
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
                    this.parentType.get()
                            .get(),
                    Optional.ofNullable(this.childType.getValue())
                            .flatMap(Holder::getOpt),
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
        public Optional<FileForm> getFileForm() {
            return this.parentType()
                    .getForm()
                    .or(() -> this.childTypeOpt.flatMap(PackContentType::getForm));
        }

        public List<IFullName> getFullNames() {
            return this.childTypeOpt()
                    .<List<IFullName>>map(childType -> List.of(this.parentType(), childType))
                    .orElseGet(() -> Collections.singletonList(this.parentType()));
        }
    }
}
