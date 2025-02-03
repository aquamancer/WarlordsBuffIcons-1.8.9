package com.aquamancer.warlordsbufficons.statuses;

import java.util.*;

/**
 * Represents the raw action bar data displayed in-game. <br>
 * Map.Entry[displayed name, displayed duration remaining]
 */
public class ActionBarStatuses {
    private List<Map.Entry<String, Integer>> buffs;
    private List<Map.Entry<String, Integer>> debuffs;
    public ActionBarStatuses() {
        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
    }
    public ActionBarStatuses(List<Map.Entry<String, Integer>> buffs, List<Map.Entry<String, Integer>> debuffs) {
        this.buffs = buffs;
        this.debuffs = debuffs;
    }

    public List<Map.Entry<String, Integer>> getBuffs() {
        return buffs;
    }

    public List<Map.Entry<String, Integer>> getDebuffs() {
        return debuffs;
    }
    @Override
    public boolean equals(Object statuses) {
        if (statuses instanceof ActionBarStatuses) {
            return this.buffs.equals(((ActionBarStatuses) statuses).getBuffs()) && this.debuffs.equals(((ActionBarStatuses) statuses).getDebuffs());
        } else {
            return false;
        }
    }

    /**
     * Calculates and returns the indexes of statuses that have been removed. This method groups each consecutive status
     * in old with the same name. Then, compares each group to a window of the same size of recent. If all elements'
     * names are not equal, indexes of old are added to deletions. If some elements in the window of recent match,
     * matches each status with the same name in recent with the status in mirrored with the closest duration as
     * the one displayed. The elements in mirrored that are not matched are added to deletions.<br>
     * Usage of this method assumes that new buffs are always appended on the right.
     * @param recent recent raw action bar data
     * @param old raw action bar data before the action bar update event
     * @param mirrored a mirror of the action bar before the update, with statuses instead of raw data
     * @return indexes of statuses that have been removed in recent
     */
    public static Set<Integer> getDeletions(List<Map.Entry<String, Integer>> old, List<Map.Entry<String, Integer>> recent, List<Status> mirrored) {
        Set<Integer> deletions = new HashSet<>();
        // Need to group consecutive old statuses to identify which one was actually removed in case one of them gets
        // removed in recent. Otherwise, it will always think that the latter buff(s) got removed.
        List<List<Map.Entry<String, Integer>>> oldGroups = groupConsecutive(old); 
        // index of the status in recent that the old statuses are trying to match with
        int recentIndex = 0;
        // tracks all elements within old
        int oldCounter = 0;
        for (int i = 0; i < oldGroups.size(); i++) {
            List<Map.Entry<String, Integer>> recentWindow = recent.subList(recentIndex, Math.min(recent.size(), recentIndex + oldGroups.get(i).size()));
            if (allMatch(oldGroups.get(i), recentWindow)) {
                recentIndex += recentWindow.size();
            } else if (noneMatch(oldGroups.get(i), recentWindow)) {
                for (int r = 0; r < oldGroups.get(i).size(); r++) deletions.add(oldCounter + r);
            } else {
                /*
                 * oldGroups.get(i) window sees: [a, a, a]
                 * recentWindow window sees: [a, a, b]
                 */
                String name = oldGroups.get(i).get(0).getKey();
                // indexes of oldGroups.get(i) marked as being the closest matches to the durations of the remaining
                // elements in recentWindow
                Set<Integer> oldMatched = new HashSet<>();
                for (int j = 0; j < recentWindow.size(); j++) {
                    if (!recentWindow.get(j).getKey().equals(name)) break; // iterate for all values of the correct name
                    long displayedRemainingDuration = recentWindow.get(j).getValue() * 1000 + 500; // 500 = midpoint
                    // get status whose remaining duration is closest to displayedRemainingDuration, and mark it as
                    // a match.
                    int closestMatch = -1;
                    long smallestDelta = Long.MAX_VALUE;
                    for (int k = oldCounter; k < oldCounter + oldGroups.get(i).size(); k++) {
                        if (oldMatched.contains(k)) continue;
                        long delta = Math.abs(mirrored.get(k).getRemainingDuration() - displayedRemainingDuration);
                        if (delta < smallestDelta) {
                            smallestDelta = delta;
                            closestMatch = k;
                        }
                    }
                    if (closestMatch != -1) // this check is technically not needed, because there are always supposed to be more of the same status in old than in recent
                        oldMatched.add(closestMatch);
                    recentIndex++; // check will be on index "b" at the end of this loop
                }
                // add all statuses in oldGroups.get(i) that are not marked as match to deletions
                for (int j = oldCounter; j < oldCounter + oldGroups.get(i).size(); j++) {
                    if (!oldMatched.contains(j)) deletions.add(j);
                }
            }
            oldCounter += oldGroups.get(i).size();
        }
        return deletions;
    }
    public static List<List<Map.Entry<String, Integer>>> groupConsecutive(List<Map.Entry<String, Integer>> input) {
        List<List<Map.Entry<String, Integer>>> result = new ArrayList<>();
        if (input.isEmpty()) return result;
        List<Map.Entry<String, Integer>> currentBucket = new ArrayList<>();
        currentBucket.add(input.get(0));
        for (int i = 1; i < input.size(); i++) {
            if (input.get(i).getKey().equals(currentBucket.get(0).getKey())) {
                currentBucket.add(input.get(i));
            } else {
                // ship the currentBucket
                result.add(currentBucket);
                currentBucket = new ArrayList<>();
                currentBucket.add(input.get(i));
            }
        }
        result.add(currentBucket);
        return result;
    }
    private static boolean allMatch(List<Map.Entry<String, Integer>> input, List<Map.Entry<String, Integer>> compareTo) {
        if (!input.isEmpty() && compareTo.isEmpty()) return false;
        if (input.size() > compareTo.size()) return false;
        for (int i = 0; i < input.size(); i++) {
            if (!input.get(i).getKey().equals(compareTo.get(i).getKey()))
                return false;
        }
        return true;
    }
    private static boolean noneMatch(List<Map.Entry<String, Integer>> input, List<Map.Entry<String, Integer>> compareTo) {
        if (!input.isEmpty() && compareTo.isEmpty()) return true;
        return !input.get(0).getKey().equals(compareTo.get(0).getKey());
    }
    public static List<Map.Entry<String, Integer>> getAdditions(List<Map.Entry<String, Integer>> old, List<Map.Entry<String, Integer>> recent, Set<Integer> indexesOfDeletions) {
        return recent.subList(Math.min(recent.size(), old.size() - indexesOfDeletions.size()), recent.size());
    }
}
