package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import com.google.gson.JsonElement;
import javafx.beans.property.BooleanProperty;

public interface IValueContent<C extends IValueContent<C>> {
    BooleanProperty changedProperty();

    BooleanProperty validProperty();

    void load(JsonElement jsonElement);

    JsonElement save();

    void persist();

    void reset();

    C withRequired(boolean required);
}
