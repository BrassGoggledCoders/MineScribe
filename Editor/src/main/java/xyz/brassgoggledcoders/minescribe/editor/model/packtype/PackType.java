package xyz.brassgoggledcoders.minescribe.editor.model.packtype;

import com.google.gson.JsonObject;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKey;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKeys;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;

public enum PackType implements IPackType {
    DATA_PACK(InfoKeys.DATA_PACK_VERSION, "Data Pack", "data", "forge:resource_pack_format"),
    RESOURCE_PACK(InfoKeys.RESOURCE_PACK_VERSION, "Resource Pack", "assets", "forge:data_pack_format");

    private final InfoKey<Integer> packKey;
    private final String name;
    private final String folderName;
    private final String extraKeyName;

    PackType(InfoKey<Integer> packKey, String name, String folderName, String extraKeyName) {
        this.packKey = packKey;
        this.name = name;
        this.folderName = folderName;
        this.extraKeyName = extraKeyName;
    }

    @Override
    public int getPackVersion() {
        return InfoRepository.getInstance().getValue(packKey);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFolderName() {
        return this.folderName;
    }

    @Override
    public void addPackMetaJson(JsonObject packObject) {
        packObject.addProperty(
                this.extraKeyName,
                InfoRepository.getInstance()
                        .getValue(this.packKey)
        );
    }
}
