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
    private boolean isHypixelDebuff;
    private int indexOnActionBar;
    private boolean premature;
    private int prematureTimeout;
    // millis
    private long initialDuration;
    private long remainingDuration;
    private long activeDuration;
    private long timeAdded;
    
    private int maxStacks;
    private int stackDeltaMillis;
    public Status(JsonObject fields) {
        
    }
    public Status(String actionBarName, boolean isHypixelDebuff, boolean premature) {
        
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

