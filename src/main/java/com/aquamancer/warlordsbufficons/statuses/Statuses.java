package com.aquamancer.warlordsbufficons.statuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All isDebuff parameters refer to "is debuff according to the action bar." This is needed to determine
 * which mirrored list to insert the status to.
 */
public class Statuses {
    /**
     * "master" list that tracks all statuses on the player.
     */
    private List<Status> buffs;
    private List<Status> debuffs;
    /**
     * Lists of statuses that have been added instantly on chat identification. We have a separate list to handle
     * in case the status that was added due to a chat event did not end up being reflected in the action bar's
     * next status update. (ex. cleansed before the action bar updated).
     */
    private List<Status> prematureStatuses;
    /**
     * Contains references to elements in allStatuses, but only contains ones that are currently displayed
     * in the action bar, and is kept in the same order.<br>
     * Where: action bar = actionBarMirrorBuffs * actionBarMirrorDebuffs
     */
    private List<Status> mirroredBuffs;
    private List<Status> mirroredDebuffs;

    /**
     * Lists of statuses that are actually displayed by the program. Used for functionality of removeSoft (remove
     * status from displayed list but still present in the mirrored; ALTERNATIVE: removeSoft just sets enabled
     * field to false), and if the user configs to not display the status.
     */
    private List<Status> displayedBuffs;
    private List<Status> displayedDebuffs;

    /**
     * Map&lt;universalName, &lt;rolling sum, n&gt;&gt; that stores the time it takes for each
     * status to tick down once. This is used to calculate the actual initial durations of statuses.
     */
    private Map<String, MedianTracker> experimentalDurations;

    private static final int REMOVE_THRESHOLD_MIN = 150; // todo make configurable
    
    public Statuses() {
        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
        this.mirroredBuffs = new ArrayList<>();
        this.mirroredDebuffs = new ArrayList<>();
        this.experimentalDurations = new HashMap<>();
        this.prematureStatuses = new ArrayList<>();
    }

    /**
     * If premature, adds to prematureStatuses. Else if there is an existing premature status with the same name
     * of the incoming status, adds the premature status to the lists instead. Else add to the lists.
     * @param status the incoming/new status to add
     * @param isActionBarDebuff whether the displayed action bar classifies the new status as a debuff
     * @param premature if the new status is a premature status
     */
    public void add(Status status, boolean isActionBarDebuff, boolean premature) {
        if (premature) {
            this.prematureStatuses.add(status);
            return;
        }
        // try to match with a queued premature status with the same name
        for (int i = 0; i < this.prematureStatuses.size(); i++) {
            // if the name of status to be added = name of premature status
            if (status.getUniversalName().equals(this.prematureStatuses.get(i).getUniversalName())) {
                this.prematureStatuses.get(i).setDisplayedDuration(status.getDisplayedDuration());
                this.addToAll(this.prematureStatuses.get(i), isActionBarDebuff);
                this.prematureStatuses.remove(i);
                return;
            }
        }
        this.addToAll(status, isActionBarDebuff);
    }
    private void addToAll(Status status, boolean isActionBarDebuff) {
        if (status.isDebuff()) {
            this.debuffs.add(status);
            if (status.iconEnabled()) displayedDebuffs.add(status);
        } else {
            this.buffs.add(status);
            if (status.iconEnabled()) displayedBuffs.add(status);
        }
        if (isActionBarDebuff) {
            this.mirroredDebuffs.add(status);
        } else {
            this.mirroredBuffs.add(status);
        }
    }


    public void processNewActionBarStatus(List<Map.Entry<String, Integer>> actionBarStatuses, boolean isActionBarDebuff) {
        for (Map.Entry<String, Integer> actionBarStatus : actionBarStatuses) {
            Status status = StatusFactory.createStatusFromActionBarName(
                    actionBarStatus.getKey(),
                    StatusFactory.calculateInitialDuration(actionBarStatus, experimentalDurations),
                    actionBarStatus.getValue()
                    );
            this.add(status, isActionBarDebuff, false);
        }
    }

    /**
     * Adds custom statuses that are guaranteed to not be reflected by the action bar.
     * @param status the custom status to be added
     */
    public void processNewCustomChatStatus(Status status) {
        if (status.isDebuff()) {
            this.debuffs.add(status);
            if (status.iconEnabled()) displayedDebuffs.add(status);
        } else {
            this.buffs.add(status);
            if (status.iconEnabled()) displayedBuffs.add(status);
        }
    }
    public void processNewPrematureStatus(Status status) {
        // add() already handles which mirrored list to add to when a matching new action bar status is found.
        // therefore we don't need isDebuff here.
        
        this.prematureStatuses.add(status);
    }

    /**
     * Removes the Status at the index of the mirrored list from the mirrored list and the master list. If the remaining
     * duration on the status is <= REMOVE_THRESHOLD_MIN, it is only removed from the master list, so the status will
     * expire on its own. Otherwise, it is abruptly removed from the displayed list.
     * @param indexOnActionBar
     * @param isDebuff
     */
    public void remove(int indexOnActionBar, boolean isDebuff, boolean cancelIcon) {
        Status removed;
        if (isDebuff) {
            removed = this.mirroredDebuffs.remove(indexOnActionBar);
            this.debuffs.remove(removed);
            if (cancelIcon || removed.getRemainingDuration() > REMOVE_THRESHOLD_MIN) this.displayedDebuffs.remove(removed);
        } else {
            removed = this.mirroredBuffs.remove(indexOnActionBar);
            this.buffs.remove(removed);
            if (cancelIcon || removed.getRemainingDuration() > REMOVE_THRESHOLD_MIN) this.displayedBuffs.remove(removed);
        }
    }
    public void clearPrematureStatuses() {
        this.prematureStatuses.clear();
    }
    public boolean contains(Status status) {
        return this.buffs.contains(status) || this.debuffs.contains(status);
    }

    public List<Status> getBuffs() {
        return buffs;
    }

    public List<Status> getDebuffs() {
        return debuffs;
    }

    public List<Status> getMirroredBuffs() {
        return mirroredBuffs;
    }

    public List<Status> getMirroredDebuffs() {
        return mirroredDebuffs;
    }
    public Map<String, MedianTracker> getExperimentalDurations() {
        return this.experimentalDurations;
    }
}
