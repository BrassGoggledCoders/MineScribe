package xyz.brassgoggledcoders.minescribe.editor.service.editoritem;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.throwingproviders.CheckedProvider;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.service.tab.IEditorTabService;

@Singleton
public class EditorItemProvider implements Provider<IEditorItemService> {

    @Inject
    public EditorItemProvider(IEditorTabService editorTabService, Provider<Project> projectProvider) {
        FileHandler.initialize(projectProvider.get(), editorTabService);
    }

    @Override
    public IEditorItemService get() {
        return FileHandler.getInstance();
    }
}
