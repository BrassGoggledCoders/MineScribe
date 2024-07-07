package xyz.brassgoggledcoders.minescribe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.model.Pack;
import xyz.brassgoggledcoders.minescribe.model.PackRepository;
import xyz.brassgoggledcoders.minescribe.model.ProjectPath;
import xyz.brassgoggledcoders.minescribe.model.ProjectPathAnchor;
import xyz.brassgoggledcoders.minescribe.registry.Registry;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackService.class);

    private final Registry<PackRepository> packRepositoryRegistry;
    private final ProjectService projectService;

    @Autowired
    public PackService(Registry<PackRepository> packRepositoryRegistry, ProjectService projectService) {
        this.packRepositoryRegistry = packRepositoryRegistry;
        this.projectService = projectService;
    }

    public List<Pack> getImportedPacks() {
        return List.of();
    }

    public List<Pack> getPacksForImport() {
        List<Pack> packs = new ArrayList<>();
        Path projectPath = this.projectService.getProject()
                .projectPath();

        for (RegistryHolder<PackRepository> registryHolder : packRepositoryRegistry) {
            PackRepository packRepository = registryHolder.getValue();
            if (packRepository != null) {
                List<Path> repositoryPaths = getRepositoryPathsFor(packRepository.path(), projectPath);

                for (Path repositoryPath : repositoryPaths) {
                    try (DirectoryStream<Path> packDirectories = Files.newDirectoryStream(repositoryPath, this::validPack)) {
                        for(Path packPath : packDirectories) {
                            packs.add(new Pack(
                                    packPath,
                                    packRepository.packTypes()
                            ));
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to find packs for repository {} and path {}", registryHolder.getId(), repositoryPath, e);
                    }
                }
            }
        }
        return packs;
    }

    public void importPack(Pack pack) {

    }

    private boolean validPack(Path path) {
        return Files.isRegularFile(path.resolve("pack.mcmeta"));
    }

    private List<Path> getRepositoryPathsFor(ProjectPath path, Path project) {
        ProjectPathAnchor projectPathAnchor = path.anchor();

        List<Path> anchorPaths = new ArrayList<>();
        if (projectPathAnchor != null) {
            anchorPaths = switch (projectPathAnchor) {
                case ROOT -> List.of(project);
                case NAMESPACE -> Collections.emptyList();
                case SAVES -> List.of(project.resolve("saves"));
            };
        }
        List<Path> paths = new ArrayList<>();
        String[] directories = path.getDirectories();
        for (Path anchorPath : anchorPaths) {
            if (Files.isDirectory(anchorPath)) {
                paths.addAll(this.getRepositoryPathsFor(anchorPath, directories, 0));
            }
        }
        return paths;
    }

    private List<Path> getRepositoryPathsFor(Path path, String[] directories, int depth) {
        String currentDirectory = directories[depth];
        List<Path> paths = new ArrayList<>();
        if (currentDirectory.equals("*")) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, Files::isDirectory)) {
                for (Path childDirectory : directoryStream) {
                    if (depth + 1 >= directories.length) {
                        paths.add(childDirectory);
                    } else {
                        paths.addAll(this.getRepositoryPathsFor(childDirectory, directories, depth + 1));
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to create directory stream for path {}", path, e);
            }
        } else {
            Path childPath = path.resolve(currentDirectory);
            if (Files.isDirectory(childPath)) {
                if (depth + 1 >= directories.length) {
                    paths.add(childPath);
                } else {
                    paths.addAll(this.getRepositoryPathsFor(childPath, directories, depth + 1));
                }
            }
        }

        return paths;
    }
}
