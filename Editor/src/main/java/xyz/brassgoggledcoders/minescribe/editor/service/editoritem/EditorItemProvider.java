package xyz.brassgoggledcoders.minescribe.editor.service.editoritem;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.service.tab.IEditorTabService;

@Singleton
public class EditorItemProvider implements Provider<IEditorItemService> {

    @Inject
    public EditorItemProvider(IEditorTabService editorTabService, Project project,
                              Registry<ResourceId, PackRepositoryLocation> repositoryLocations) {
        FileHandler.initialize(project, editorTabService, repositoryLocations);
    }

    @Override
    public IEditorItemService get() {
        return FileHandler.getInstance();
    }
}
