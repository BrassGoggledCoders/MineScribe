package xyz.brassgoggledcoders.minescribe.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;


public class MineScribeJsonHelper {
    public static String getAsString(JsonObject pJson, String pMemberName) {
        if (pJson.has(pMemberName)) {
            return convertToString(pJson.get(pMemberName), pMemberName);
        } else {
            throw new JsonSyntaxException("Missing " + pMemberName + ", expected to find a string");
        }
    }

    public static String convertToString(JsonElement pJson, String pMemberName) {
        if (pJson.isJsonPrimitive()) {
            return pJson.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + pMemberName + " to be a string");
        }
    }

    public static int getAsInt(JsonObject pJson, String pMemberName, int pFallback) {
        return pJson.has(pMemberName) ? convertToInt(pJson.get(pMemberName), pMemberName) : pFallback;
    }

    public static int convertToInt(JsonElement pJson, String pMemberName) {
        if (pJson.isJsonPrimitive() && pJson.getAsJsonPrimitive().isNumber()) {
            return pJson.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + pMemberName + " to be a Int");
        }
    }

    public static JsonArray getAsJsonArray(JsonObject pJson, String pMemberName) {
        if (pJson.has(pMemberName)) {
            return convertToJsonArray(pJson.get(pMemberName), pMemberName);
        } else {
            throw new JsonSyntaxException("Missing " + pMemberName + ", expected to find a JsonArray");
        }
    }

    public static JsonArray convertToJsonArray(JsonElement pJson, String pMemberName) {
        if (pJson.isJsonArray()) {
            return pJson.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + pMemberName + " to be a JsonArray");
        }
    }

    public static List<String> getAsStrings(JsonElement jsonElement) {
        List<String> stringList = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                if (arrayElement.isJsonPrimitive()) {
                    stringList.add(arrayElement.getAsString());
                }
            }
        } else if (jsonElement.isJsonPrimitive()) {
            stringList.add(jsonElement.getAsString());
        }
        return stringList;
    }
}
