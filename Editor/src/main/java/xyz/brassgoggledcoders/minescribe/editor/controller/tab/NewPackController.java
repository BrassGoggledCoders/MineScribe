package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.model.validators.SelectionLengthValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.google.gson.JsonObject;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.PackDirectoryEditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.form.SmallerSimpleListViewControl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewPackController {
    @FXML
    public AnchorPane formContainer;
    public Button submit;

    private EditorItem parentItem;
    private Form form;

    @FXML
    public void initialize() {
        form = Form.of(Group.of(
                Field.ofMultiSelectionType(List.of("Data Pack", "Resource Pack"), List.of(0, 1))
                        .label("Pack Type")
                        .render(SmallerSimpleListViewControl::new)
                        .id("packType")
                        .validate(SelectionLengthValidator.atLeast(1, "Must pick at least 1 Pack Type")),
                Field.ofStringType("")
                        .label("Name")
                        .id("name")
                        .validate(StringLengthValidator.atLeast(1, "Name Cannot Be Empty")),
                Field.ofStringType("")
                        .label("Description")
                        .id("description")
                        .multiline(true)
                        .validate(StringLengthValidator.atLeast(1, "Description Cannot be Empty"))
        ));
        FormRenderer formRenderer = new FormRenderer(form);

        AnchorPane.setTopAnchor(formRenderer, 0D);
        AnchorPane.setBottomAnchor(formRenderer, 0D);
        AnchorPane.setLeftAnchor(formRenderer, 0D);
        AnchorPane.setRightAnchor(formRenderer, 0D);

        formContainer.getChildren().add(formRenderer);
        formRenderer.requestLayout();

        submit.disableProperty().bind(Bindings.not(form.validProperty()));
    }

    public void setParentItem(EditorItem parentItem) {
        this.parentItem = parentItem;
    }

    public void onSubmit() {
        if (this.parentItem != null) {

            Map<String, Field<?>> fieldMap = this.form.getGroups()
                    .stream()
                    .map(Group::getElements)
                    .flatMap(List::stream)
                    .filter(e -> e instanceof Field)
                    .map(e -> (Field<?>) e)
                    .collect(Collectors.toMap(Element::getID, field -> field));

            JsonObject jsonObject = new JsonObject();
            Field<?> field = fieldMap.get("name");
            if (field instanceof DataField<?,?,?> dataField && dataField.getValue() instanceof String name) {
                
            }

        }
    }
}
