package com.aquamancer.warlordsbufficons.statuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

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
    private Map<String, Map.Entry<Integer, Integer>> experimentalInitialDurations;

    private static final int REMOVE_THRESHOLD_MIN = 150; // todo make configurable
    
    public Statuses() {
        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
        this.mirroredBuffs = new ArrayList<>();
        this.mirroredDebuffs = new ArrayList<>();
        this.experimentalInitialDurations = new HashMap<>();
        this.prematureStatuses = new ArrayList<>();
    }

    public void add(Status status, boolean isActionBarDebuff, boolean premature) {
        if (premature) {
            this.prematureStatuses.add(status);
            return;
        }
        // try to match with a queued premature status with the same name
        for (int i = 0; i < this.prematureStatuses.size(); i++) {
            // if the name of status to be added = name of premature status
            if (status.getUniversalName().equals(this.prematureStatuses.get(i).getUniversalName())) {
                this.add(this.prematureStatuses.get(i), isActionBarDebuff);
                this.prematureStatuses.remove(i);
                return;
            }
        }
        this.add(status, isActionBarDebuff);
    }
    public void add(Status status, boolean isActionBarDebuff) {
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


    public void processNewStatusesFromActionBar(List<Map.Entry<String, Integer>> actionBarStatuses, boolean isDebuff) {
        for (Map.Entry<String, Integer> actionBarStatus : actionBarStatuses) {
            Status status = StatusFactory.fromActionBarName(
                    actionBarStatus.getKey(),
                    StatusFactory.calculateInitialDuration(actionBarStatus, experimentalInitialDurations)
                    );

        }
    }

    /**
     * For statuses that are guaranteed to not be reflected by the action bar.
     * @param status
     */
    public void processStatusFromChat(Status status) {
        
    }
    public void processPrematureStatus(PrematureStatus status, boolean isDebuff) {
        
    }
    public void remove(Status status, boolean removeIcon) {
        if (status.isHypixelDebuff()) {
            this.debuffs.remove(status);
            if (removeIcon) this.displayedDebuffs.remove(status);
        } else {
            this.buffs.remove(status);
            if (removeIcon) this.displayedBuffs.remove(status);
        }
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
            removed = this.debuffs.remove(indexOnActionBar);
            if (cancelIcon || removed.getRemainingDuration() > REMOVE_THRESHOLD_MIN) this.displayedDebuffs.remove(removed);
        } else {
            removed = this.buffs.remove(indexOnActionBar);
            if (cancelIcon || removed.getRemainingDuration() > REMOVE_THRESHOLD_MIN) this.displayedBuffs.remove(removed);
        }
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

    public Map<Status, ScheduledFuture<?>> getIconCancelTasks() {
        return iconCancelTasks;
    }
}
