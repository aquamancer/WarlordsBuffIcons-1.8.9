package com.github.aquamancer.warlordsbufficons.statuses;

import java.awt.*;

public class Status {
    private int actionBarIndex;
    private double initialDuration;
    private double remainingDuration;
    private long timeAddedMillis;
    private int maxStacks;
    private int stackDeltaMillis;
    private Color border;
    public Status(DebuffEnum debuff) {
        this.border = Color.RED;
    }
    public Status(BuffEnum buff) {
        this.border = Color.GREEN;
    }

    public void setRemainingDuration(double duration) {
        if (this.initialDuration < duration) {
            this.initialDuration = duration;
        }
        this.remainingDuration = duration;
    }
}

