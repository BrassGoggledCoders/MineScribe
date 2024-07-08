package xyz.brassgoggledcoders.minescribe.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.control.Either;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {
    private final UUID uuid;
    private final SimpleListProperty<String> importedPacks;

    public Project() {
        this(UUID.randomUUID(), new ArrayList<>());
    }

    @JsonCreator
    public Project(
            @JsonProperty("uuid") UUID uuid,
            @JsonProperty("importedPacks") List<String> importedPacks
    ) {
        this.uuid = uuid;
        this.importedPacks = new SimpleListProperty<>(this, "importedPacks", FXCollections.observableArrayList(importedPacks));
    }

    public UUID getUuid() {
        return uuid;
    }

    public ListProperty<String> importedPacksProperty() {
        return importedPacks;
    }

    public List<String> getImportedPacks() {
        return importedPacks;
    }

    public static Either<Path, String> checkPath(@Nullable Path path, boolean newProject) {
        if (path != null && Files.exists(path) && Files.isDirectory(path)) {
            if (path.getFileName().endsWith(".minescribe")) {
                path = path.getParent();
            }

            Path minescribeChild = path.resolve(".minescribe");
            if (Files.exists(minescribeChild) && Files.isDirectory(minescribeChild)) {
                Path loadComplete = minescribeChild.resolve(".load-complete");
                if (Files.exists(loadComplete) && Files.isRegularFile(loadComplete)) {
                    if (newProject || Files.exists(path.resolve("minescribe_project.json"))) {
                        return Either.left(path);
                    } else {
                        return Either.right("Failed to find existing project");
                    }
                } else {
                    return Either.right(".minescribe directory does not contain required resources, run 'minescribe generate' command in Minecraft");
                }
            } else {
                return Either.right("Directory does not container a valid .minescribe directory");
            }
        } else {
            return Either.right("Minescribe directory does not exist or is not a directory");
        }
    }
}
