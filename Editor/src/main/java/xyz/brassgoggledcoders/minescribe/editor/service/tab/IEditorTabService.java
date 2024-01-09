package xyz.brassgoggledcoders.minescribe.editor.service.tab;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface IEditorTabService {

    <TAB extends Tab> TAB openTab(String typeName, @Nullable Path path);

    void setEditorTabPane(TabPane tabPane);
}
