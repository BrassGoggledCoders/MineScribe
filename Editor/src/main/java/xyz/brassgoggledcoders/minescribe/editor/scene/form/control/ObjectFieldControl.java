package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ObjectField;

public class ObjectFieldControl extends MineScribeSimpleControl<ObjectField> {
    @Override
    public void layoutParts() {
        super.layoutParts();

        this.layoutForNode(this.field.getEditorForm());
    }
}
