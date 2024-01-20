package xyz.brassgoggledcoders.minescribe.editor.service.tab;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.IFileTab;
import xyz.brassgoggledcoders.minescribe.editor.service.fxml.IFXMLService;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.Optional;

@Singleton
public class EditorTabService implements IEditorTabService {
    private final IFXMLService fxmlService;
    private final Provider<Project> projectProvider;

    private WeakReference<TabPane> tabPaneRef;

    @Inject
    public EditorTabService(IFXMLService fxmlService, Provider<Project> projectProvider) {
        this.fxmlService = fxmlService;
        this.projectProvider = projectProvider;
        this.tabPaneRef = new WeakReference<>(null);
    }

    @Override
    public <TAB extends Tab> TAB openTab(String typeName, @Nullable Path path) {
        TabPane tabPane = tabPaneRef.get();
        if (tabPane != null) {
            boolean createNewTab = true;
            if (path != null) {
                Optional<Tab> existingTab = tabPane.getTabs()
                        .stream()
                        .filter(tab -> {
                            if (tab instanceof IFileTab fileTab) {
                                return path.equals(fileTab.pathProperty().get());
                            }
                            return false;
                        })
                        .findFirst();

                if (existingTab.isPresent()) {
                    existingTab.ifPresent(tabPane.getSelectionModel()::select);
                    createNewTab = false;
                }
            }

            if (createNewTab) {
                TAB newTab = fxmlService.load(Application.class.getResource("tab/" + typeName + ".fxml"));
                if (newTab != null) {
                    tabPane.getTabs()
                            .add(newTab);
                    tabPane.getSelectionModel()
                            .select(newTab);

                    if (path != null) {
                        newTab.setText(path.getFileName()
                                .toString()
                        );
                        Project project = projectProvider.get();
                        if (project != null) {
                            project.addOpenTab(path);
                        }

                        if (newTab instanceof IFileTab fileTab) {
                            fileTab.pathProperty()
                                    .setValue(path);
                        }
                    }
                }

                return newTab;
            }
        }

        return null;
    }

    @Override
    public void setEditorTabPane(TabPane tabPane) {
        this.tabPaneRef = new WeakReference<>(tabPane);
    }
}
