package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class StatusFactory {
    /**
     * the universal name's mapping and its action bar name mapping point to the same JsonObject
     */
    // todo make sure program doesn't break if config is changed while icons are displayed
    // todo maybe null checks for retrieving values from json
    private static Map<String, JsonObject> fromUniversalName;
    
    private static Map<String, String> toUniversalName;
    private static Map<String, String> stackingEnabled;
    
    private static final String MANUAL_INITIAL_DURATION = "manualInitialDuration"; // required for all objects in json otherwise nullpointerexception
    private static final String EXPERIMENTAL_INITIAL_DURATION = "experimentalInitialDuration"; // required for all objects in json otherwise nullpointerexception
    
    public static void loadJson(JsonObject json) {
        fromUniversalName = new HashMap<>();
        toUniversalName = new HashMap<>();
        
        for (Map.Entry<String, JsonElement> status : json.entrySet()) {
            JsonObject statusFields = status.getValue().getAsJsonObject();
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

    public static int calculateInitialDuration(Map.Entry<String, Integer> actionBarStatus, Map<String, Map.Entry<Integer, Integer>> experimental) {
        String universalName = toUniversalName(actionBarStatus.getKey());
        Integer displayedRemainingDuration = actionBarStatus.getValue();

        // manual initial duration is the highest priority
        int manualInitialDuration = fromUniversalName.get(universalName).get(MANUAL_INITIAL_DURATION).getAsInt();
        if (withinIntervalInclusive(displayedRemainingDuration, manualInitialDuration)) return manualInitialDuration;
        // then try current session experimental data
        if (experimental.containsKey(universalName)) {
            int avg = experimental.get(universalName).getKey() / experimental.get(universalName).getValue();
            if (withinIntervalInclusive(displayedRemainingDuration, avg)) return avg;
        }
        // then try previous session experimental data (from json)
        int previousExperimentalDuration = fromUniversalName.get(universalName).get(EXPERIMENTAL_INITIAL_DURATION).getAsInt();
        if (withinIntervalInclusive(displayedRemainingDuration, previousExperimentalDuration)) return previousExperimentalDuration;
        // default
        return displayedRemainingDuration * 1000 - 500;
    }
    
    /**
     * Determines if the displayedRemainingDuration can be representative of query remaining duration, inclusive.<br>
     * Being inclusive on the right limit is technically incorrect, but is needed for manual initial duration
     * inputs. For example, time warp's initial duration is 5.00 seconds. This means the exact moment it is casted
     * for a split second,
     * the action bar should display 6, but it doesn't because there's a delay, which is why we need to be inclusive,
     * so that 5.00 seconds will be captured when the action bar displays 5 seconds. <br>
     * 
     * This method is used in calculateInitialDuration to check if a status that has just been added can rely on
     * the manual and guessed initial durations, meaning these pre-set durations are possible, 
     * given the displayed initial duration.
     * @param displayedRemainingDuration seconds
     * @param query millis
     * @return true if query is in the interval [displayedRemainingDuration * 1000 - 1000, displayedRemainingDuration * 1000]
     */
    public static boolean withinIntervalInclusive(int displayedRemainingDuration, int query) {
        return query >= displayedRemainingDuration * 1000 - 1000 && query <= displayedRemainingDuration * 1000;
    }

    /**
     * 
     * @param universalName
     * @param experimental
     * @return
     */
    public static int calculateInitialDuration(String universalName, Map<String, Map.Entry<Integer, Integer>> experimental) {
        int manualInitialDuration = fromUniversalName.get(universalName).get(MANUAL_INITIAL_DURATION).getAsInt();
        if (manualInitialDuration > 0)
            return manualInitialDuration;
        
        if (experimental.containsKey(universalName))
            return experimental.get(universalName).getKey() / experimental.get(universalName).getValue();
        
        return -1;
    }
    public static boolean isCustomStatus(String universalName) {
        return fromUniversalName.get(universalName).get("custom").getAsBoolean();
    }
}