package com.aquamancer.warlordsbufficons.statuses;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.Map;

public class Status {
    private final boolean isUnrecognized;
    // todo make configurable
    private static int DISCREPANCY_THRESHOLD_TO_SYNC = 250;
    private static int RECOVERY_BUFFER = 150;
    private String universalName;

    /*
     * Fields from statuses.json
     */
    boolean isDebuff, iconEnabled, isCustom;
    int[] borderRGBA;
    /*
     * Custom fields for runtime
     */
    private int initialDisplayedDuration;
    private int displayedDuration;
    // millis
    private int initialDuration;
    private int remainingDuration;
    private double elapsed;

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
    private Status(int initialDuration, boolean isDebuff, boolean iconEnabled, boolean isCustom, int[] borderRGBA) {
        this.timeAddedMillis = Minecraft.getSystemTime();
        this.hasExperimentalDurationBeenLogged = false;
        this.isUnrecognized = false;
        this.initialDuration = initialDuration;
        this.remainingDuration = initialDuration;
        this.elapsed = 0;
        // parse json
        this.isDebuff = isDebuff;
        this.iconEnabled = iconEnabled;
        this.isCustom = isCustom;
        this.borderRGBA = borderRGBA;
    }

    /**
     * Creates an unrecognized Status.
     * @param initialDuration
     * @param initialDisplayedDuration
     */
    protected Status(int initialDuration, int initialDisplayedDuration) {
        this.timeAddedMillis = Minecraft.getSystemTime();
        this.hasExperimentalDurationBeenLogged = false;
        this.initialDuration = initialDuration;
        this.remainingDuration = initialDuration;
        this.elapsed = 0;
        this.initialDisplayedDuration = initialDisplayedDuration;
        
        this.isUnrecognized = true;
    }

    /**
     * Creates a custom/premature Status.
     * @param universalName
     * @param initialDuration
     * @param isDebuff
     * @param iconEnabled
     * @param isCustom
     * @param borderRGBA
     */
    protected Status(String universalName, int initialDuration, boolean isDebuff, boolean iconEnabled, boolean isCustom, int[] borderRGBA) {
        this(initialDuration, isDebuff, iconEnabled, isCustom, borderRGBA);
        this.universalName = universalName;
    }
    protected Status(String universalName, int initialDuration, int initialDisplayedDuration, boolean isDebuff, boolean iconEnabled, boolean isCustom, int[] borderRGBA) {
        this(initialDuration, isDebuff, iconEnabled, isCustom, borderRGBA);
        this.universalName = universalName;
        this.initialDisplayedDuration = initialDisplayedDuration;
        this.displayedDuration = initialDisplayedDuration;
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
            if (!this.hasExperimentalDurationBeenLogged && !this.isUnrecognized) {
                long currentTimeMillis = Minecraft.getSystemTime();
                // calculate the precise initial duration
                int precision = (int) (currentTimeMillis - this.timeAddedMillis);
                int experimentalInitialDuration = displayedDuration * 1000 + precision;

                MedianTracker.updateMedianTracker(experimentalInitialDuration, this.universalName, experimentalInitialDurations);
                this.hasExperimentalDurationBeenLogged = true;
            }

            if (Math.abs(displayedDuration * 1000 - this.remainingDuration) > DISCREPANCY_THRESHOLD_TO_SYNC) {
                this.remainingDuration = displayedDuration * 1000;
            }
            this.displayedDuration = displayedDuration;
        }
    }
    public boolean isUnrecognized() {
        return this.isUnrecognized;
    }
    public long getRemainingDuration() {
        return remainingDuration;
    }
    // todo remove after testing
    public void setRemainingDuration(int millis) {
        if (millis > this.initialDuration) {
            this.initialDuration = millis;
        }
        this.remainingDuration = millis;
    }
    public int getInitialDuration() {
        return this.initialDuration;
    }
    public int getDisplayedDuration() {
        return this.displayedDuration;
    }
    public void setDisplayedDuration(int seconds) {
        this.displayedDuration = seconds;
    }
    public int getInitialDisplayedDuration() {
        return this.initialDisplayedDuration;
    }
    public long getTimeAddedMillis() {
        return this.timeAddedMillis;
    }
    public double getElapsed() {
        return this.elapsed;
    }
    public String getUniversalName() {
        return this.universalName;
    }
    public int[] getBorderRGBA() {
        return this.borderRGBA;
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
    public void timePassed(int millis) {
        this.remainingDuration -= millis;
        this.elapsed = (this.initialDuration - this.remainingDuration) / (double) this.initialDuration;
    }
}

