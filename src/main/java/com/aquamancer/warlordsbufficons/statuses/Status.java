package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Map;

public class Status {
    // todo make configurable
    private static int DISCREPANCY_THRESHOLD_TO_SYNC = 250;
    private static int RECOVERY_BUFFER = 150;
    private String universalName;
    private String actionBarName;
    private boolean isDebuff, iconEnabled, isCustom;
    private Color border;
    /*
     * Custom fields for runtime
     */
    private int initialDisplayedDuration;
    private int displayedDuration;
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
    private boolean hasExperimentalDurationBeenLogged; // whether the status duration has ticked down once and logged
    private int experimentalDuration; // duration after tick down + timed time it took to tick down
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
        this.hasExperimentalDurationBeenLogged = false;
    }
    public Status(String universalName, int initialDuration, int initialDisplayedDuration, JsonObject jsonData) {
        this.universalName = universalName;
        this.initialDisplayedDuration = initialDisplayedDuration;
        this.displayedDuration = initialDisplayedDuration;
        this.initialDuration = initialDuration;
        this.remainingDuration = initialDuration;
        this.timeAddedMillis = System.currentTimeMillis();
        this.hasExperimentalDurationBeenLogged = false;
        // todo parse json, create fields, fill fields
    }

    /**
     * Syncs the remaining duration with the displayed duration.
     * @param displayedDuration the current displayed duration
     */
    public void sync(int displayedDuration, Map<String, MedianTracker> experimentalInitialDurations) {
        if (displayedDuration == this.displayedDuration) {
            // sync to + recovery buffer if the duration drops low enough that if the duration changed now,
            // it would have to sync
            if (this.remainingDuration < displayedDuration * 1000 - DISCREPANCY_THRESHOLD_TO_SYNC)
                this.remainingDuration = displayedDuration * 1000 - 1000 + RECOVERY_BUFFER;
        } else {
            if (!this.hasExperimentalDurationBeenLogged) {
                long currentTimeMillis = System.currentTimeMillis();
                // calculate the precise initial duration
                int precision = (int) (currentTimeMillis - this.timeAddedMillis);
                int experimentalInitialDuration = displayedDuration * 1000 + precision;

                MedianTracker medianTracker = experimentalInitialDurations.get(this.universalName);
                if (medianTracker == null) {
                    experimentalInitialDurations.put(universalName, new MedianTracker(experimentalInitialDuration));
                } else {
                    medianTracker.add(experimentalInitialDuration);
                }
                this.hasExperimentalDurationBeenLogged = true;
            }

            if (Math.abs(displayedDuration * 1000 - this.remainingDuration) > DISCREPANCY_THRESHOLD_TO_SYNC) {
                this.remainingDuration = displayedDuration * 1000;
            }

            this.displayedDuration = displayedDuration;
        }
    }
    public long getRemainingDuration() {
        return remainingDuration;
    }
    public int getDisplayedDuration() {
        return this.displayedDuration;
    }
    public void setDisplayedDuration(int seconds) {
        this.displayedDuration = seconds;
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
    public boolean hasExperimentalDurationBeenLogged() {
        return hasExperimentalDurationBeenLogged;
    }
}

