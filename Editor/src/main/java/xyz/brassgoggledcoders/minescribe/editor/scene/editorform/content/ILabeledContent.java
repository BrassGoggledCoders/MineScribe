package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import javafx.scene.control.Label;
import org.jetbrains.annotations.Nullable;

public interface ILabeledContent<C> {
    @Nullable
    Label getLabel();

    default C withLabel(String label) {
        return this.withLabel(new Label(label));
    }

    C withLabel(Label label);
}
