package xyz.brassgoggledcoders.minescribe.registry;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RegistryHolder<T> {
    private final RegistryId id;
    private final SimpleObjectProperty<T> value;
    private final SimpleObjectProperty<RegistryRoot> registryRoot;

    public RegistryHolder(RegistryId id) {
        this.id = id;
        this.value = new SimpleObjectProperty<>(this, "value");
        this.registryRoot = new SimpleObjectProperty<>(this, "registryRoot");
    }

    public RegistryId getId() {
        return id;
    }

    public ReadOnlyObjectProperty<T> valueProperty() {
        return value;
    }

    public T getValue() {
        return value.getValue();
    }

    public void setValue(T value) {
        this.value.setValue(value);
    }

    public RegistryRoot getRegistryRoot() {
        return registryRoot.getValue();
    }

    public ReadOnlyObjectProperty<RegistryRoot> registryRootProperty() {
        return registryRoot;
    }

    public void setRegistryRoot(RegistryRoot registryRoot) {
        this.registryRoot.setValue(registryRoot);
    }
}
