package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Map;

public class Status {
    private String universalName;
    private String actionBarName;
    private boolean isDebuff, iconEnabled;
    private Color border;
    /*
     * Custom fields for runtime
     */
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

    private long timeAdded;

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
        this.timeAdded = System.currentTimeMillis();
        this.initialDurationExperimentalLogged = false;
        // todo parse json, create fields, fill fields
    }

    /**
     * Syncs the remaining duration. 
     * @param remainingDuration
     */
    public long sync(int remainingDuration) {
        return 0;
    }
    public long sync(Map.Entry<String, Integer> status) {
        return 0;
    }
    public void setRemainingDuration(long duration) {
        if (this.initialDuration < duration) {
            this.initialDuration = duration;
        }
        this.remainingDuration = duration;
    }

    public String getActionBarName() {
        return actionBarName;
    }

    public long getRemainingDuration() {
        return remainingDuration;
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
}

