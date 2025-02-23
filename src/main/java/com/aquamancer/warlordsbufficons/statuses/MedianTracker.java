package com.aquamancer.warlordsbufficons.statuses;

import java.util.Collections;
import java.util.PriorityQueue;

public class MedianTracker {
    private PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder()); // max heap
    private PriorityQueue<Integer> high = new PriorityQueue<>(); // min heap
    
    public MedianTracker() {};
    public MedianTracker(int... trials) {
        for (int trial : trials) {
            this.add(trial);
        }
    }
    public void add(Integer trial) {
        if (low.isEmpty() || trial <= low.peek()) {
            low.add(trial);
        } else {
            high.add(trial);
        }
        // maintain median for odd = low.peek()
        if (low.size() > high.size() + 1) {
            high.add(low.poll());
        } else if (high.size() > low.size()) {
            low.add(high.poll());
        }
    }
    public int getMedian() {
        if (low.isEmpty()) return -1;
        
        if (low.size() == high.size()) {
            return (low.peek() + high.peek()) / 2;
        } else {
            return low.peek();
        }
    }
}
