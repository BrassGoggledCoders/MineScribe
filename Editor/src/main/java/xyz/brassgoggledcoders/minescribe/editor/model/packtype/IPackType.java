package xyz.brassgoggledcoders.minescribe.editor.model.packtype;

import com.google.gson.JsonObject;

public interface IPackType {
    int getPackVersion();

    String getName();

    String getFolderName();

    void addPackMetaJson(JsonObject packObject);
}
