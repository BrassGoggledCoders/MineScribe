package xyz.brassgoggledcoders.minescribe.model.component;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LiteralTextContent implements ITextContent {
    private final StringProperty text;

    public LiteralTextContent(String text) {
        this.text = new SimpleStringProperty(this, "text", text);
    }


    @Override
    public ReadOnlyStringProperty textProperty() {
        return this.text;
    }
}
