package xyz.brassgoggledcoders.minescribe.registry;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.service.JsonService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Registry<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Registry.class);

    private final String name;
    private final String[] directory;
    private final Class<T> tClass;

    private final ObservableList<RegistryHolder<T>> values;
    private final ObservableMap<RegistryId, RegistryHolder<T>> mappedValues;

    private final ObservableSet<RegistryRoot> registryRoots;

    private final JsonService jsonService;

    public Registry(String name, String directory, Class<T> tClass, JsonService jsonService) {
        this.name = name;
        this.directory = directory.replace("\\", "/")
                .split("/");
        this.tClass = tClass;
        this.jsonService = jsonService;

        this.values = FXCollections.observableArrayList(registryHolder -> new ReadOnlyObjectProperty[]{
                registryHolder.valueProperty()
        });
        this.mappedValues = FXCollections.observableHashMap();
        this.registryRoots = FXCollections.observableSet();
    }

    public RegistryHolder<T> getValue(RegistryId registryId) {
        return this.mappedValues.get(registryId);
    }

    public Class<T> getValueClass() {
        return this.tClass;
    }

    public void addRegistryRoot(RegistryRoot registryRoot) {
        if (this.registryRoots.add(registryRoot)) {
            this.loadFromRegistryRoot(registryRoot);
        }
    }

    private void loadFromRegistryRoot(RegistryRoot registryRoot) {
        int loaded = 0;
        Path loadPath = registryRoot.path()
                .resolve("minescribe");
        if (Files.exists(loadPath)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(loadPath)) {
                for (Path path : directoryStream) {
                    if (Files.isDirectory(path)) {
                        loaded += readFromRepositoryPath(registryRoot, path.getFileName().toString(), path, 0);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("[{}] Failed to load registry root {}", this.name, registryRoot, e);
            }
            if (loaded > 0) {
                LOGGER.info("[{}] Loaded {} values from {}", this.name, loaded, registryRoot.path());
            }
        }
    }

    private int readFromRepositoryPath(RegistryRoot registryRoot, String namespace, Path parent, int depth) {
        int loaded = 0;
        String directoryPath = this.directory[depth];
        parent = parent.resolve(directoryPath);

        if (Files.isDirectory(parent)) {
            int newDepth = depth + 1;
            if (newDepth >= this.directory.length) {
                loaded += readFromPathFolders(registryRoot, namespace, parent, "");
            } else {
                loaded += readFromRepositoryPath(registryRoot, namespace, parent, depth + 1);
            }
        }

        return loaded;
    }

    private int readFromPathFolders(RegistryRoot registryRoot, String namespace, Path parent, String idPath) {
        int loaded = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, Files::exists)) {
            for (Path path : stream) {
                String newIdPath = idPath;
                if (!newIdPath.isEmpty()) {
                    newIdPath += "/";
                }
                if (Files.isDirectory(path)) {
                    loaded += readFromPathFolders(registryRoot, namespace, parent, newIdPath + path.getFileName());
                } else if (Files.isRegularFile(path)) {
                    RegistryId registryId = new RegistryId(
                            namespace,
                            newIdPath + path.getFileName()
                                    .toString()
                                    .replace(".json", "")
                    );
                    if (readValue(registryRoot, registryId, path)) {
                        loaded++;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("[{}] Failed to load values from directory for namespace {} and path {}", this.name, namespace, parent, e);
        }
        return loaded;
    }

    private boolean readValue(RegistryRoot registryRoot, RegistryId registryId, Path path) {
        boolean doLoad = true;
        if (this.mappedValues.containsKey(registryId)) {
            RegistryHolder<T> holder = this.mappedValues.get(registryId);

            if (registryRoot.compareTo(holder.getRegistryRoot()) < 0) {
                doLoad = false;
            }
        }

        if (doLoad) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                T value = jsonService.readValue(inputStream, this.tClass);
                if (value != null) {
                    RegistryHolder<T> registryHolder = this.mappedValues.computeIfAbsent(
                            registryId,
                            this::createRegistryHolder
                    );

                    registryHolder.setValue(value);
                    registryHolder.setRegistryRoot(registryRoot);

                    return true;
                }
            } catch (IOException e) {
                LOGGER.error("[{}] Failed to load values from file {}", this.name, path, e);
            }
        }

        return false;
    }

    private RegistryHolder<T> createRegistryHolder(RegistryId registryId) {
        RegistryHolder<T> holder = new RegistryHolder<>(registryId);
        this.values.add(holder);
        return holder;
    }

    public void clear() {
        this.values.clear();
        this.registryRoots.clear();
        this.mappedValues.clear();
    }
}
