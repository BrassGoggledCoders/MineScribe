package xyz.brassgoggledcoders.minescribe.editor.scene.tab;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tab;

import java.nio.file.Path;

@DefaultProperty("content")
public class FileTab extends Tab implements IFileTab {

    private ObjectProperty<Path> path;

    @Override
    public ObjectProperty<Path> pathProperty() {
        if (this.path == null) {
            this.path = new SimpleObjectProperty<>(this, null);
        }
        return this.path;
    }
}
