package xyz.brassgoggledcoders.minescribe.api.event;

import net.minecraftforge.eventbus.api.Event;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;

import java.util.ArrayList;
import java.util.List;

public class GatherPackRepositoryLocationsEvent extends Event {
    private final List<PackRepositoryLocation> packRepositoryLocations;

    public GatherPackRepositoryLocationsEvent() {
        this.packRepositoryLocations = new ArrayList<>();
    }

    public void register(PackRepositoryLocation packRepositoryLocation) {
        this.packRepositoryLocations.add(packRepositoryLocation);
    }

    public List<PackRepositoryLocation> getPackRepositoryLocations() {
        return packRepositoryLocations;
    }
}
