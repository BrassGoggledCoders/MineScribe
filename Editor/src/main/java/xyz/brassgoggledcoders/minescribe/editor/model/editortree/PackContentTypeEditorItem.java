package xyz.brassgoggledcoders.minescribe.editor.model.editortree;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.editor.registry.PackContentTypeRegistry;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackContentTypeEditorItem extends EditorItem {
    private final List<PackContentType> validTypes;
    private final List<PackContentType> childTypes;
    private final Map<PackContentType, List<PackContentType>> subTypes;
    private final int index;

    public PackContentTypeEditorItem(String name, Path path, List<PackContentType> validTypes) {
        this(name, path, validTypes, 0);
    }

    public PackContentTypeEditorItem(String name, Path path, List<PackContentType> validTypes, int index) {
        super(name, path);
        this.validTypes = validTypes;
        this.index = index;
        this.childTypes = new ArrayList<>();
        for (PackContentType packContentType : validTypes) {
            if (packContentType.path().getNameCount() > index + 1) {
                childTypes.add(packContentType);
            }
        }
        this.subTypes = new HashMap<>();
        for (PackContentType packContentType : validTypes) {
            List<PackContentType> packContentSubTypes = PackContentTypeRegistry.getInstance()
                    .getPackContentSubTypes(packContentType);
            if (!packContentSubTypes.isEmpty()) {
                this.subTypes.put(packContentType, packContentSubTypes);
            }
        }
    }

    @Override
    public boolean isValid() {
        return !validTypes.isEmpty();
    }

    @Override
    public @NotNull List<EditorItem> createChildren() {
        List<File> childrenFiles = this.getChildrenFiles();
        List<EditorItem> editorItems = new ArrayList<>();
        for (File childFile : childrenFiles) {
            Path childPath = childFile.toPath();
            if (childFile.isDirectory()) {
                EditorItem editorItem = null;
                if (!childTypes.isEmpty()) {
                    List<PackContentType> typesForFile = childTypes.parallelStream()
                            .filter(packContentType -> childPath.endsWith(packContentType.path().subpath(0, index + 1)))
                            .toList();

                    if (!typesForFile.isEmpty()) {
                        editorItem = new PackContentTypeEditorItem(childFile.getName(), childPath, typesForFile, index + 1);
                    }
                }
                if (editorItem == null && !this.subTypes.isEmpty()) {
                    Map<PackContentType, List<PackContentType>> subTypesForFile = subTypes.entrySet()
                            .parallelStream()
                            .filter(packContentType ->packContentType.getValue()
                                    .parallelStream()
                                    .anyMatch(packContentSubType -> childPath.endsWith(packContentSubType.path().getName(0)))
                            )
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    if (!subTypesForFile.isEmpty()) {
                        editorItem = new PackContentSubTypeEditorItem(childFile.getName(), childPath, subTypesForFile);
                    }
                }
                if (editorItem != null) {
                    editorItems.add(editorItem);
                }
            } else {
                editorItems.add(validTypes.stream()
                        .findFirst()
                        .flatMap(PackContentType::form)
                        .<EditorItem>map(form -> new FormEditorFileEditorItem(childFile.getName(), childPath, form))
                        .orElseGet(() -> new MissingFormFileEditorItem(childFile.getName(), childPath))
                );
            }
        }

        return editorItems;
    }
}
