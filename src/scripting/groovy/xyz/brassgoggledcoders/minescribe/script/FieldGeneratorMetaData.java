package xyz.brassgoggledcoders.minescribe.script;

import javafx.scene.control.Control;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FieldGeneratorMetaData {
    private String name;
    private String description;
    private Supplier<Control> controlSupplier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Supplier<Control> getControlSupplier() {
        return controlSupplier;
    }

    public void setControlSupplier(Supplier<Control> controlSupplier) {
        this.controlSupplier = controlSupplier;
    }
}
