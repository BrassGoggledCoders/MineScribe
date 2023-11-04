package xyz.brassgoggledcoders.minescribe.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GatherFormListsEvent extends GatherMineScribeRegistryObjectEvent<FormList, FormList> {

    public GatherFormListsEvent() {
        super(formList -> new ResourceLocation(formList.id().namespace(), formList.id().path()), Function.identity());
    }
}
