package xyz.brassgoggledcoders.minescribe.editor.scene.form;

import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.geometry.Insets;

public class ZeroPaddedFormRenderer extends FormRenderer {
    public ZeroPaddedFormRenderer(Form form) {
        super(form);
    }

    @Override
    public String getUserAgentStylesheet() {
        return null;
    }

    @Override
    public void layoutParts() {
        super.layoutParts();
        this.setPadding(Insets.EMPTY);
        this.sections.forEach(section -> section.getStyleClass().add("no-borders"));
        getChildren().setAll(sections);
    }

    public Form getForm() {
        return this.form;
    }
}
