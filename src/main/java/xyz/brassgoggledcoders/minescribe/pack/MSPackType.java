package xyz.brassgoggledcoders.minescribe.pack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.function.BiFunction;

public enum MSPackType {
    RESOURCE(
            "assets",
            (gameDir, locationPath, packName) -> locationPath.resolve(packName),
            (path, name) -> createPackInfo(path, name + " minescribe assets", PackType.CLIENT_RESOURCES)
    ),
    DATA(
            "data",
            (gameDir, locationPath, packName) -> locationPath.resolve(packName),
            (path, name) -> createPackInfo(path, name + " minescribe data", PackType.SERVER_DATA)
    );

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final String typeFolderName;
    private final Function3<Path, Path, String, Path> packResolver;
    private final BiFunction<Path, String, Either<Throwable, String>> onCreate;

    MSPackType(String typeFolderName, Function3<Path, Path, String, Path> packResolver, BiFunction<Path, String, Either<Throwable, String>> onCreate) {
        this.typeFolderName = typeFolderName;
        this.packResolver = packResolver;
        this.onCreate = onCreate;
    }

    public Path resolvePack(Path gameDir, Path parent, String packName) {
        return this.packResolver.apply(gameDir, parent, packName);
    }

    public Path resolveType(Path packPath) {
        return packPath.resolve(typeFolderName);
    }

    public Either<Throwable, String> onCreate(Path packPath, String packName) {
        return onCreate.apply(packPath, packName);
    }

    private static Either<Throwable, String> createPackInfo(Path path, String description, PackType packType) {
        JsonObject packObject = new JsonObject();
        packObject.addProperty("pack_format", packType.getVersion(SharedConstants.getCurrentVersion()));
        packObject.addProperty("description", description);

        JsonObject parentObject = new JsonObject();
        parentObject.add(PackMetadataSection.SERIALIZER.getMetadataSectionName(), packObject);

        try (
                Writer writer = new FileWriter(path.resolve("pack.mcmeta").toFile());
                JsonWriter jsonWriter = GSON.newJsonWriter(writer)
        ) {
            GSON.toJson(parentObject, jsonWriter);
            return Either.right("Created Pack Info Successful");
        } catch (IOException e) {
            return Either.left(e);
        }
    }
}
