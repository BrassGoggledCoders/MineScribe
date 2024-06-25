package xyz.brassgoggledcoders.minescribe.project;

import io.vavr.control.Either;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public record Project(
        UUID uuid,
        Path projectPath
) {
    public Project(Path projectPath) {
        this(UUID.randomUUID(), projectPath);
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
