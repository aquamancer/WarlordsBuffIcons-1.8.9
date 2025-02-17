package com.aquamancer.warlordsbufficons.statuses;

public class DurationPair {
    private int sum, n;
    public DurationPair(int duration) {
        this.sum = duration;
        this.n = 1;
    }
    public void add(int duration) {
        this.sum += duration;
        n++;
    }
    public int getAverage() {
        return this.sum / this.n;
    }
}
