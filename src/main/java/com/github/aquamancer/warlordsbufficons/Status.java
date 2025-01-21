package com.github.aquamancer.warlordsbufficons;

import com.github.aquamancer.warlordsbufficons.statuses.BuffEnum;
import com.github.aquamancer.warlordsbufficons.statuses.DebuffEnum;

import java.awt.*;

public class Status {
    private double initialDuration;
    private double remainingDuration;
    private Color border;
    public Status(DebuffEnum debuff) {
        this.border = Color.RED;
    }
    public Status(BuffEnum buff) {
        this.border = Color.GREEN;
    }
}
