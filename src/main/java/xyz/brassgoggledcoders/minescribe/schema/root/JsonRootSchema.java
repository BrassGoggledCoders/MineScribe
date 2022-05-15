package xyz.brassgoggledcoders.minescribe.schema.root;

import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import xyz.brassgoggledcoders.minescribe.api.schema.root.IRootSchema;

import java.nio.file.Path;

@JsonAdapter(JsonRootSchemaSerializer.class)
public record JsonRootSchema(
        ResourceLocation id,
        JsonObject jsonObject
) implements IRootSchema {

    @Override
    public String getType() {
        return jsonObject().getAsJsonPrimitive("type")
                .getAsString();
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public Path getPath() {
        return FMLPaths.GAMEDIR.get()
                .resolve(".minescribe")
                .resolve(this.id().getNamespace())
                .resolve(this.id().getPath() + ".json");
    }
}
