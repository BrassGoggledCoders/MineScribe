package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class FieldContent<C extends FieldContent<C>> {

    public abstract Node getNode();

    public void setPseudoClass(PseudoClass pseudoClass, boolean on) {
        this.getNode().pseudoClassStateChanged(pseudoClass, on);
        if (this.getNode() instanceof Parent parent) {
            parent.getChildrenUnmodifiable()
                    .forEach(child -> {
                        if (child instanceof IFieldContentNode fieldControlNode) {
                            fieldControlNode.getFieldContent()
                                    .setPseudoClass(pseudoClass, on);
                        } else {
                            child.pseudoClassStateChanged(pseudoClass, on);
                        }
                    });
        }
    }

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
