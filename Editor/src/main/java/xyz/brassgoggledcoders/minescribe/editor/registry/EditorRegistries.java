package xyz.brassgoggledcoders.minescribe.editor.registry;

import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
import xyz.brassgoggledcoders.minescribe.editor.file.FileUpdate;

import java.nio.file.Path;

public class EditorRegistries {

    public static Registry<ResourceId, PackContentType> getContentTypes() {
        return Registries.getRegistry(RegistryNames.CONTENT_TYPES);
    }

    public static EditorFormFieldRegistry getEditorFormFieldRegistry() {
        Registry<String, EditorFormFieldTransform<?, ?>> registry = Registries.getRegistry(RegistryNames.EDITOR_FIELD_TRANSFORMS);
        if (registry instanceof EditorFormFieldRegistry editorFormFieldTransforms) {
            return editorFormFieldTransforms;
        } else {
            throw new IllegalStateException("Failed to find Editor Field Transform Registry");
        }
    }

    public static SerializerTypeRegistry getSerializerTypes() {
        Registry<ResourceId, SerializerType> registry = Registries.getRegistry(RegistryNames.SERIALIZER_TYPES);
        if (registry instanceof SerializerTypeRegistry serializerTypeRegistry) {
            return serializerTypeRegistry;
        } else {
            throw new IllegalStateException("Failed to find serializer type registry");
        }
    }

    public static Registry<ResourceId, ObjectType> getObjectTypes() {
        return Registries.getRegistry(RegistryNames.OBJECT_TYPES);
    }

    public static void load(Path mineScribeRoot) {
        for (IRegistryProviderService service : Registries.REGISTRY_PROVIDER_SERVICE_LOADER) {
            service.load(mineScribeRoot);
        }
    }

    public static void addSourcePath(Path sourcePath) {
        for (IRegistryProviderService service : Registries.REGISTRY_PROVIDER_SERVICE_LOADER) {
            service.addSourcePath(sourcePath);
        }
    }

    public static void tryUpdate(FileUpdate fileUpdate) {
        for (IRegistryProviderService service : Registries.REGISTRY_PROVIDER_SERVICE_LOADER) {
            for (Registry<?, ?> registry : service.getRegistries()) {
                if (registry instanceof IFileUpdateListener fileUpdateListener) {
                    fileUpdateListener.fileUpdated(fileUpdate);
                }
            }
        }
    }
}
