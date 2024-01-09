package xyz.brassgoggledcoders.minescribe.editor.scene.tab;

import javafx.beans.property.ObjectProperty;

import java.nio.file.Path;

public interface IFileTab {
    ObjectProperty<Path> pathProperty();
}
