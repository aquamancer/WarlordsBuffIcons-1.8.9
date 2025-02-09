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
    // todo make sure program doesn't break if config is changed while icons are displayed
    public static Map<String, JsonObject> fromUniversalName;
    
    private static Map<String, String> toUniversalName;
    private static Map<String, String> stackingEnabled;
    
    public static void loadJson(JsonObject json) {
        for (Map.Entry<String, JsonElement> status : json.entrySet()) {
            JsonObject statusFields = status.getValue().getAsJsonObject();
            if (!statusFields.get("enabled").getAsBoolean()) continue;
            fromUniversalName.put(status.getKey(), statusFields);
            toUniversalName.put(statusFields.get("actionBarName").getAsString(), status.getKey());
        }
    }
    public static Status fromUniversalName(String universalName, int initialDuration) {
        return new Status(universalName, initialDuration, fromUniversalName.get(universalName));
    }
    public static Status fromActionBarName(String actionBarName, int initialDuration) {
        return new Status(toUniversalName(actionBarName), initialDuration, fromUniversalName.get(toUniversalName(actionBarName)));
    }
    public static String toUniversalName(String actionBarName) {
        return toUniversalName.get(actionBarName);
    }
}