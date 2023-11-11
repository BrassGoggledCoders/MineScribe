package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import javafx.scene.Node;

public abstract class FieldContent<C extends FieldContent<C>> {

    public abstract Node getNode();

    public void finishSetup() {
        this.getNode()
                .getStyleClass()
                .add("field-content");
    }

    @SuppressWarnings("unchecked")
    public C withId(String id) {
        this.getNode()
                .setId(id);
        return (C) this;
    }
}
