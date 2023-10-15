package xyz.brassgoggledcoders.minescribe.editor.scene.form;

import com.dlsc.formsfx.view.controls.SimpleListViewControl;

public class SmallerSimpleListViewControl<T> extends SimpleListViewControl<T> {

    @Override
    public void layoutParts() {
        super.layoutParts();
        this.listView.setPrefHeight(80);
    }
}
