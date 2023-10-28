package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.scene.layout.VBox;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.ZeroPaddedFormRenderer;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ObjectField;

public class ObjectFieldControl extends MineScribeSimpleControl<ObjectField> {

    private VBox fieldPane;
    private FormRenderer formRenderer;
    private FormRenderer serializerFormRenderer;

    @Override
    public void initializeParts() {
        super.initializeParts();

        this.fieldPane = new VBox();
        formRenderer = new ZeroPaddedFormRenderer(this.field.getForm());
        formRenderer.getStyleClass().add("top-border-padding");
        this.fieldPane.getStyleClass().add("borders");
        this.fieldPane.getChildren().add(formRenderer);
        if (this.field.getSerializerForm() != null) {
            this.serializerFormRenderer = new ZeroPaddedFormRenderer(this.field.getSerializerForm());
            this.serializerFormRenderer.getStyleClass().add("bottom-border-padding");
            this.fieldPane.getChildren().add(this.serializerFormRenderer);
        } else {
            formRenderer.getStyleClass().add("bottom-border-padding");
        }
    }

    @Override
    public void layoutParts() {
        super.layoutParts();

        this.layoutForNode(this.fieldPane);
    }


    @Override
    public void setupBindings() {
        super.setupBindings();

        this.field.serializerSetupProperty().addListener((observable, oldValue, newValue) -> {
            this.fieldPane.getChildren().remove(this.serializerFormRenderer);
            this.serializerFormRenderer = null;
            if (newValue != null) {
                this.serializerFormRenderer = new ZeroPaddedFormRenderer(this.field.getSerializerForm());
                this.formRenderer.getStyleClass().remove("bottom-border-padding");
                this.serializerFormRenderer.getStyleClass().add("bottom-border-padding");
                this.fieldPane.getChildren().add(this.serializerFormRenderer);
            } else {
                formRenderer.getStyleClass().add("bottom-border-padding");
            }
        });
    }
}
