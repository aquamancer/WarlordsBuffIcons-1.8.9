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
    public static Status createStatusFromUniversalName(String universalName, int initialDuration, int initialDisplayedDuration) {
        return new Status(universalName, initialDuration, initialDisplayedDuration, fromUniversalName.get(universalName));
    }
    public static Status createStatusFromActionBarName(String actionBarName, int initialDuration, int initialDisplayedDuration) {
        return new Status(toUniversalName(actionBarName), initialDuration, initialDisplayedDuration, fromUniversalName.get(toUniversalName(actionBarName)));
    }

    public static Status createCustomStatusFromUniversalName(String universalName, int initialDuration) {
        return new Status(universalName, initialDuration, fromUniversalName.get(universalName));
    }
    public static String toUniversalName(String actionBarName) {
        return toUniversalName.get(actionBarName);
    }

    /**
     * Checks the user config, current runtime experimental data, and previous runtime experimental data for
     * the initial duration of the Status, and verifies these values are even possible given the duration
     * displayed.
     * @param actionBarStatus
     * @param experimental
     * @return In order from highest to lowest priority: <ul>
     *     <li>duration set in the user config, if set</li>
     *     <li>median of runtime experimental data, if any</li>
     *     <li>the previous runtime's experimental median, if any</li>
     *     <li>displayed duration * 1000 - 500, otherwise</li>
     * </ul>
     */
    public static int calculateInitialDuration(Map.Entry<String, Integer> actionBarStatus, Map<String, MedianTracker> experimental) {
        String universalName = toUniversalName(actionBarStatus.getKey());
        Integer displayedRemainingDuration = actionBarStatus.getValue();

        int manualInitialDuration = fromUniversalName.get(universalName).get(MANUAL_INITIAL_DURATION).getAsInt();
        if (withinIntervalInclusive(displayedRemainingDuration, manualInitialDuration))
            return manualInitialDuration;
        
        MedianTracker experimentalMedian = experimental.get(universalName);
        if (experimentalMedian != null) {
            if (withinIntervalInclusive(displayedRemainingDuration, experimentalMedian.getMedian()))
                return experimentalMedian.getMedian();
        }
        
        int previousExperimentalDuration = fromUniversalName.get(universalName).get(EXPERIMENTAL_INITIAL_DURATION).getAsInt();
        if (withinIntervalInclusive(displayedRemainingDuration, previousExperimentalDuration))
            return previousExperimentalDuration;
        // default
        return displayedRemainingDuration * 1000 - 500;
    }
    
    /**
     * Checks the user config manual initial duration and the current runtime experimental duration for 
     * the initial duration of the Status.
     * @param universalName the Status to retrieve the initial duration for
     * @param experimental current runtime experimental duration data
     * @return <ul>
     *     In order from highest to lowest priority:
     *     <li>duration set in the user config, if set</li>
     *     <li>median of runtime experimental data, if any</li>
     *     <li>the previous runtime's experimental median, if any</li>
     *     <li>-1, otherwise</li>
     * </ul>
     */
    public static int calculateInitialDuration(String universalName, Map<String, MedianTracker> experimental) {
        int manualInitialDuration = fromUniversalName.get(universalName).get(MANUAL_INITIAL_DURATION).getAsInt();
        if (manualInitialDuration > 0)
            return manualInitialDuration;

        if (experimental.containsKey(universalName))
            return experimental.get(universalName).getMedian();

        int previousExperimentalDuration = fromUniversalName.get(universalName).get(EXPERIMENTAL_INITIAL_DURATION).getAsInt();
        if (previousExperimentalDuration > 0)
            return previousExperimentalDuration;

        return -1;
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


    public static boolean isCustomStatus(String universalName) {
        return fromUniversalName.get(universalName).get("custom").getAsBoolean();
    }
}