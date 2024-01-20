package xyz.brassgoggledcoders.minescribe.editor.scene.tab;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.IFullName;

@DefaultProperty("content")
public class EditorFormTab extends FileTab {
    private ObjectProperty<FileForm> fileForm;
    private ListProperty<IFullName> parents;

    public EditorFormTab() {
        super();
    }

    public ObjectProperty<FileForm> fileFormProperty() {
        if (this.fileForm == null) {
            this.fileForm = new SimpleObjectProperty<>(this, null);
        }
        return this.fileForm;
    }

    public ListProperty<IFullName> parentsProperty() {
        if (this.parents == null) {
            this.parents = new SimpleListProperty<>(this, null);
        }
        return this.parents;
    }
}
