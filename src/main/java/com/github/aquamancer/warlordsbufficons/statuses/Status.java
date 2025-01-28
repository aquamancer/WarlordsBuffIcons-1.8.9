package com.github.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;

import java.awt.*;

public class Status {
    private String universalName;
    private int indexOnActionBar;
    private double initialDuration;
    private double remainingDuration;
    private long timeAddedMillis;
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
        
    }
    public void setRemainingDuration(double duration) {
        if (this.initialDuration < duration) {
            this.initialDuration = duration;
        }
        this.remainingDuration = duration;
    }
}

