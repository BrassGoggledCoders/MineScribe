package xyz.brassgoggledcoders.minescribe.model.view;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import xyz.brassgoggledcoders.minescribe.model.Pack;
import xyz.brassgoggledcoders.minescribe.model.PackType;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

public class PackImportView {
    private final SimpleBooleanProperty toImport;
    private final ObjectProperty<Pack> pack;
    private final SimpleListProperty<RegistryHolder<PackType>> typesToImport;

    public PackImportView(Pack pack) {
        this.toImport = new SimpleBooleanProperty(this, "toImport", false);
        this.pack = new SimpleObjectProperty<>(this, "pack", pack);
        this.typesToImport = new SimpleListProperty<>(FXCollections.observableArrayList(pack.packTypes()));
    }

    public BooleanProperty toImportProperty() {
        return toImport;
    }

    public ObjectProperty<Pack> packProperty() {
        return pack;
    }

    public ListProperty<RegistryHolder<PackType>> typesToImportProperty() {
        return typesToImport;
    }
}
