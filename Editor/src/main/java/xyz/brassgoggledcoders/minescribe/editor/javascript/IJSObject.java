package xyz.brassgoggledcoders.minescribe.editor.javascript;

import java.util.Map;

public interface IJSObject {
    Map<String, Object> getValues();
    MineScribeJSField<?>[] getFields();
}
