package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;

import java.awt.*;

public class Status {
    private String universalName;
    private String actionBarName;
    private boolean isHypixelDebuff;
    private int indexOnActionBar;
    // millis
    private long initialDuration;
    private long remainingDuration;
    private long activeDuration;
    private long timeAdded;
    
    private int maxStacks;
    private int stackDeltaMillis;
    private Color border;
    public Status(JsonObject fields) {
        
    }

    /**
     * Syncs the remaining duration. 
     * @param remainingDuration
     */
    public long sync(int remainingDuration) {
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

