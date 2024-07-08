package xyz.brassgoggledcoders.minescribe.service;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.model.ProjectPath;
import xyz.brassgoggledcoders.minescribe.model.ProjectPathAnchor;
import xyz.brassgoggledcoders.minescribe.model.pack.Pack;
import xyz.brassgoggledcoders.minescribe.model.pack.PackRepository;
import xyz.brassgoggledcoders.minescribe.model.pack.metadata.PackMetaDataContainer;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.registry.Registry;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Service
public class PackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackService.class);

    private final Registry<PackRepository> packRepositoryRegistry;
    private final ProjectService projectService;
    private final JsonService jsonService;

    private final SimpleListProperty<Pack> importedPacks;

    private final ObservableValue<List<Path>> importedPaths;

    @Autowired
    public PackService(Registry<PackRepository> packRepositoryRegistry, ProjectService projectService,
                       JsonService jsonService) {
        this.packRepositoryRegistry = packRepositoryRegistry;
        this.projectService = projectService;
        this.jsonService = jsonService;

        this.importedPacks = new SimpleListProperty<>(this, "importedPacks");

        this.importedPaths = this.projectService.projectProperty()
                .flatMap(Project::importedPacksProperty)
                .map(pathStrings -> pathStrings.stream()
                        .map(pathString -> {
                            Path path = Path.of(pathString);
                            if (!path.isAbsolute()) {
                                path = this.projectService.getProjectPath()
                                        .resolve(path);
                            }
                            return path;
                        })
                        .toList()
                );
    }

    public ObservableList<Pack> getImportedPacks() {
        if (this.importedPacks.getValue() == null) {
            this.importedPacks.setValue(FXCollections.observableArrayList(this.getPacks(this.importedPaths.getValue()::contains)));
        }
        return this.importedPacks;
    }

    public List<Pack> getPacksForImport() {
        return this.getPacks(Predicate.not(this.importedPaths.getValue()::contains));
    }

    private List<Pack> getPacks(Predicate<Path> load) {
        List<Pack> packs = new ArrayList<>();
        Path projectPath = this.projectService.getProjectPath();

        for (RegistryHolder<PackRepository> registryHolder : packRepositoryRegistry) {
            PackRepository packRepository = registryHolder.getValue();
            if (packRepository != null) {
                List<Path> repositoryPaths = getRepositoryPathsFor(packRepository.path(), projectPath);

                for (Path repositoryPath : repositoryPaths) {
                    try (DirectoryStream<Path> packDirectories = Files.newDirectoryStream(repositoryPath, this::validPack)) {
                        for (Path packPath : packDirectories) {
                            if (load.test(packPath)) {
                                try (InputStream inputStream = Files.newInputStream(packPath.resolve("pack.mcmeta"))) {
                                    PackMetaDataContainer container = this.jsonService.readValue(
                                            inputStream,
                                            PackMetaDataContainer.class
                                    );
                                    packs.add(new Pack(
                                            packPath,
                                            container,
                                            packRepository.packTypes()
                                    ));
                                } catch (IOException e) {
                                    LOGGER.error("Failed to load packs for path {}", packPath, e);
                                }
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to find packs for repository {} and path {}", registryHolder.getId(), repositoryPath, e);
                    }
                }
            }
        }
        return packs;
    }

    public void importPacks(List<Pack> packs) {
        this.getImportedPacks()
                .addAll(packs);
        this.projectService.importedPacks(packs.stream()
                .map(Pack::path)
                .toList()
        );
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
