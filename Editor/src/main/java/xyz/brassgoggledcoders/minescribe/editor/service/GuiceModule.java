package xyz.brassgoggledcoders.minescribe.editor.service;

import com.google.inject.Binder;
import com.google.inject.Module;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.service.editoritem.EditorItemProvider;
import xyz.brassgoggledcoders.minescribe.editor.service.editoritem.IEditorItemService;
import xyz.brassgoggledcoders.minescribe.editor.service.fxml.FXMLService;
import xyz.brassgoggledcoders.minescribe.editor.service.fxml.IFXMLService;
import xyz.brassgoggledcoders.minescribe.editor.service.page.IPageService;
import xyz.brassgoggledcoders.minescribe.editor.service.page.PageService;
import xyz.brassgoggledcoders.minescribe.editor.service.project.IProjectService;
import xyz.brassgoggledcoders.minescribe.editor.service.project.ProjectService;
import xyz.brassgoggledcoders.minescribe.editor.service.tab.EditorTabService;
import xyz.brassgoggledcoders.minescribe.editor.service.tab.IEditorTabService;

public class GuiceModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(IFXMLService.class).to(FXMLService.class);

        binder.bind(IProjectService.class).to(ProjectService.class);
        binder.bind(Project.class).toProvider(ProjectService.class);

        binder.bind(IPageService.class).to(PageService.class);

        binder.bind(IEditorTabService.class).to(EditorTabService.class);

        binder.bind(IEditorItemService.class).toProvider(EditorItemProvider.class);
    }
}
