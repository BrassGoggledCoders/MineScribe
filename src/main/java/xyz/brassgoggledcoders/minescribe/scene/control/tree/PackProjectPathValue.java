package xyz.brassgoggledcoders.minescribe.scene.control.tree;

import xyz.brassgoggledcoders.minescribe.model.pack.Pack;

public class PackProjectPathValue extends ProjectPathValue {
    private final Pack pack;

    public PackProjectPathValue(Pack pack) {
        super(
                pack.getDescription()
                        .getText(),
                pack.path()
        );
        this.pack = pack;
    }

    public Pack getPack() {
        return pack;
    }
}
