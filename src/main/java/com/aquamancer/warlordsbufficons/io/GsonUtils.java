package com.aquamancer.warlordsbufficons.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonUtils {
    public static boolean getBoolean(JsonObject data, String key, boolean defaultValue) {
        return (data != null && data.has(key) && data.get(key).isJsonPrimitive() && data.get(key).getAsJsonPrimitive().isBoolean())
                ? data.get(key).getAsBoolean()
                : defaultValue;
    }
    public static int getInt(JsonObject data, String key, int defaultValue) {
        return (data != null && data.has(key) && data.get(key).isJsonPrimitive() && data.get(key).getAsJsonPrimitive().isNumber())
                ? data.get(key).getAsInt()
                : defaultValue;
    }
    public static int getInt(JsonElement element, int defaultValue) {
        return (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())
                ? element.getAsInt()
                : defaultValue;
    }
    public static int[] getIntArray(JsonObject data, String key, int[] defaultValue) {
        if (data == null || !data.has(key) || !data.get(key).isJsonArray()) return defaultValue;
        JsonArray array = data.getAsJsonArray(key);
        int[] result = new int[defaultValue.length];
        for (int i = 0; i < defaultValue.length; i++) {
            if (i >= array.size()) {
                result[i] = defaultValue[i];
            } else {
                result[i] = getInt(array.get(i), defaultValue[i]);
            }
        }
        return result;
    }
}
