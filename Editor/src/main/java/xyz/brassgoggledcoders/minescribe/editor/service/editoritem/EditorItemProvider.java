package xyz.brassgoggledcoders.minescribe.editor.service.editoritem;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.service.tab.IEditorTabService;

@Singleton
public class EditorItemProvider implements Provider<IEditorItemService> {

    @Inject
    public EditorItemProvider(IEditorTabService editorTabService, Provider<Project> projectProvider,
                              Registry<ResourceId, PackRepositoryLocation> repositoryLocations) {
        FileHandler.initialize(projectProvider, editorTabService, repositoryLocations);
    }

    @Override
    public IEditorItemService get() {
        return FileHandler.getInstance();
    }
}
