package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;

import org.jetbrains.annotations.Nullable;

public interface IHelpTextContent {
    @Nullable
    @SuppressWarnings("unused")
    String getHelpText();

    void setHelpText(String helpText);
}
