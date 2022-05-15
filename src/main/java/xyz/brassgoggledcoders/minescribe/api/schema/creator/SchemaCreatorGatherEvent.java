package xyz.brassgoggledcoders.minescribe.api.schema.creator;

import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;

public class SchemaCreatorGatherEvent extends Event {
    private final List<ISchemaCreator> schemaCreators;

    public SchemaCreatorGatherEvent() {
        this.schemaCreators = Lists.newArrayList();
    }

    public void addSchemaCreator(ISchemaCreator schemaCreator) {
        this.schemaCreators.add(schemaCreator);
    }

    public Collection<ISchemaCreator> getSchemaCreators() {
        return schemaCreators;
    }
}
