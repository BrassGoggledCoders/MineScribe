package xyz.brassgoggledcoders.minescribe.api.schema;

import com.google.gson.annotations.SerializedName;

public interface ISchema {

    @SerializedName("type")
    String getType();
}
