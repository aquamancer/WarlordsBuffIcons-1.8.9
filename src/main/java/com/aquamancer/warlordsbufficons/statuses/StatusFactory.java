package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Disabled icons will not be present in the Maps.<br>
 */
public class StatusFactory {
    /**
     * the universal name's mapping and its action bar name mapping point to the same JsonObject
     */
    private static Map<String, JsonObject> fromUniversalName;
    private static Map<String, JsonObject> fromActionBarName;
    
    public static void loadJson(JsonObject json) {
        for (Map.Entry<String, JsonElement> status : json.entrySet()) {
            JsonObject statusFields = status.getValue().getAsJsonObject();
            if (!statusFields.get("enabled").getAsBoolean()) continue;
            fromUniversalName.put(status.getKey(), statusFields);
            fromActionBarName.put(statusFields.get("actionBarName").getAsString(), statusFields);
        }
    }
    public static Status fromUniversalName(String universalName) {
        return fromUniversalName.get(universalName).get();
    }
    public static Status fromActionBarName(String actionBarName) {
        return new Status(fromActionBarName.get(actionBarName));
    }
    public static Status createStatus(String actionBarName, boolean isHypixelDebuff, boolean premature) {
        return new Status(actionBarName, isHypixelDebuff, premature);
    }
}