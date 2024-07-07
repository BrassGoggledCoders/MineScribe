package xyz.brassgoggledcoders.minescribe.model.component;

import javafx.beans.property.ReadOnlyStringProperty;

public interface ITextContent {
    ReadOnlyStringProperty textProperty();

    default String getText() {
        return this.textProperty()
                .getValue();
    }
}
