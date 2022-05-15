package xyz.brassgoggledcoders.minescribe.schema.root;

import com.google.gson.annotations.JsonAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import xyz.brassgoggledcoders.minescribe.schema.IFileMatch;
import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;

import java.nio.file.Path;
import java.util.Map;

@JsonAdapter(value = RootSchemaSerializer.class)
public record RootSchema(
        ResourceLocation id,
        Map<String, ISchema> definitions,
        ISchema actualSchema,
        String[] fileMatch
) implements ISchema, IFileMatch {
    @Override
    public String getType() {
        return actualSchema().getType();
    }

    @Override
    public String[] getFileMatch() {
        return new String[0];
    }

    public Path getPath() {
        return FMLPaths.GAMEDIR.get()
                .resolve(".minescribe")
                .resolve(this.id().getNamespace())
                .resolve(this.id().getPath() +  ".json");
    }
}
