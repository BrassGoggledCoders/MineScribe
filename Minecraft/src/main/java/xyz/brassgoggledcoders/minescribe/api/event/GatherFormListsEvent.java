package xyz.brassgoggledcoders.minescribe.api.event;

import net.minecraftforge.eventbus.api.Event;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;

import java.util.ArrayList;
import java.util.List;

public class GatherFormListsEvent extends Event {
    private final List<FormList> formLists;

    public GatherFormListsEvent() {
        this.formLists = new ArrayList<>();
    }

    public void register(FormList packRepositoryLocation) {
        this.formLists.add(packRepositoryLocation);
    }

    public List<FormList> getFormLists() {
        return formLists;
    }
}
