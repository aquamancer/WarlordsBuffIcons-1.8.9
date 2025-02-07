package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Map;

public class Status {
    private String universalName;
    private String actionBarName;
    private Color border;

    /*
     * Custom fields for runtime
     */
    private boolean premature;
    private int prematureTimeout;
    // millis
    private long initialDuration;
    private long remainingDuration;

    /**
     * Because of upgrades, not every status's initial duration is universal. So, to get the most accurate prediction
     * of the actual initial duration, we log the real time it takes for the displayed duration to decrease once. We can
     * use the average/median to estimate the precision of the initial duration. This data is managed by
     * StatusController and also logged to statuses.json.
     */
    private boolean initialDurationExperimentalLogged; // whether the status duration has ticked down once and logged
    private long initialDurationExperimental; // duration after tick down + timed time it took to tick down

    private long timeAdded;
    
    private int maxStacks;
    private int stackDeltaMillis;
    public Status(JsonObject fields) {

    }
    public Status(String actionBarName, boolean isDebuff, boolean premature) {
        
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
    public boolean isHypixelDebuff() {
        return this.isHypixelDebuff;
    }
}

