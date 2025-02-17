package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

public class Status {
    // todo make configurable
    private static int SYNC_TOLERANCE_MILLIS = 250;
    private String universalName;
    private String actionBarName;
    private boolean isDebuff, iconEnabled, isCustom;
    private Color border;
    /*
     * Custom fields for runtime
     */
    /**
     * Tracks the last displayed duration of this status on the action bar, to track when the duration has changed.
     * This field will NOT be initialized when chat event creates a premature status or custom status. It will
     * be initialized for premature statuses when there has been a match with an action bar status addition.
     */
    private int previousDisplayedDuration;
    // millis
    private int initialDuration;
    private int remainingDuration;

    /**
     * Because of upgrades, not every status's initial duration is universal. So, to get the most accurate prediction
     * of the actual initial duration, we log the real time it takes for the displayed duration to decrease once. We can
     * use the average/median to estimate the precision of the initial duration. This data is managed by
     * StatusController and also logged to statuses.json.
     * 
     * Statuses that have been prematurely added (added from chat event) will be very accurate at guessing the
     * actual initial duration. Statuses that have been added solely on action bar data will have its guessed
     * initial duration to be shorter than it actually is, because of the delay to display on the action bar. However,
     * this does not matter, because we add the status when the action bar updates, which means it is delayed anyway,
     * so the remaining duration should be shorter anyway. Only problem is if a particular buff is added on both
     * chat event only, and action bar event only.
     */
    private boolean initialDurationExperimentalLogged; // whether the status duration has ticked down once and logged
    private int initialDurationExperimental; // duration after tick down + timed time it took to tick down
    private final long timeAddedMillis;

    /*
     * Required fields for icon to function:
     * initial duration, border color, icon path
     * universal name
     *      - initial duration logging
     * stacking info
     */
    public Status(String universalName, int initialDuration, JsonObject jsonData) {
        this.universalName = universalName;
        this.initialDuration = initialDuration;
        this.remainingDuration = initialDuration;
        this.timeAddedMillis = System.currentTimeMillis();
        this.initialDurationExperimentalLogged = false;
    }
    public Status(String universalName, int initialDuration, int initialDisplayedDuration, JsonObject jsonData) {
        this.universalName = universalName;
        this.previousDisplayedDuration = initialDisplayedDuration;
        this.initialDuration = initialDuration;
        this.remainingDuration = initialDuration;
        this.timeAddedMillis = System.currentTimeMillis();
        this.initialDurationExperimentalLogged = false;
        // todo parse json, create fields, fill fields
    }

    /**
     * Syncs the remaining duration with the displayed duration.
     * @param displayedDuration the current displayed duration
     */
    public void sync(int displayedDuration, Map<String, DurationPair> experimentalInitialDurations) {
        if (displayedDuration == this.previousDisplayedDuration || this.isCustom) return;
        // the displayed duration for this status has just changed
        // handle initial duration experimental
        if (!this.initialDurationExperimentalLogged) {
            long currentTimeMillis = System.currentTimeMillis();
            int precision = (int) (currentTimeMillis - this.timeAddedMillis);
            int experimentalInitialDuration = displayedDuration * 1000 + precision;
            
            DurationPair durationPair = experimentalInitialDurations.get(this.universalName);
            if (durationPair == null) {
                experimentalInitialDurations.put(universalName, new DurationPair(experimentalInitialDuration));
            } else {
                durationPair.add(experimentalInitialDuration);
            }
            this.initialDurationExperimentalLogged = true;
        }
        if (Math.abs(displayedDuration * 1000 - this.remainingDuration) > SYNC_TOLERANCE_MILLIS) {
            this.remainingDuration = displayedDuration * 1000;
        }
    }

    public long getRemainingDuration() {
        return remainingDuration;
    }
    public int getPreviousDisplayedDuration() {
        return this.previousDisplayedDuration;
    }
    public void setPreviousDisplayedDuration(int seconds) {
        this.previousDisplayedDuration = seconds;
    }
    public String getUniversalName() {
        return this.universalName;
    }
    public boolean isDebuff() {
        return this.isDebuff;
    }
    public boolean iconEnabled() {
        return this.iconEnabled;
    }
    public boolean isCustom() {
        return this.isCustom;
    }
}

