package xyz.brassgoggledcoders.minescribe.api.schema.root;

import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;

import java.nio.file.Path;

public interface IRootSchema extends ISchema {

    ResourceLocation getId();
    Path getPath();
}
