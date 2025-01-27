package com.github.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Disabled icons will not be present in the Maps.
 */
public class StatusFactory {
    private static Map<String, Supplier<Status>> fromUniversalName;
    private static Map<String, Supplier<Status>> fromActionBarName;
    
    public static void fromJson(JsonObject json) {
        for (Map.Entry<String, JsonElement> status : json.entrySet()) {
            JsonObject statusFields = status.getValue().getAsJsonObject();
            if (!statusFields.get("enabled").getAsBoolean()) continue;
            fromUniversalName.put(status.getKey(), () -> new Status(statusFields));
        }
    }
    public static Status createStatus(String universalName) {
        return fromUniversalName.get(universalName).get();
    }
}