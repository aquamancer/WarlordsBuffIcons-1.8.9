package com.aquamancer.warlordsbufficons.statuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class Statuses {
    /**
     * "master" list that tracks all statuses on the player.
     */
    private List<Status> buffs;
    private List<Status> debuffs;
    /**
     * Contains references to elements in allStatuses, but only contains ones that are currently displayed
     * in the action bar, and is kept in the same order.<br>
     * Where: action bar = actionBarMirrorBuffs * actionBarMirrorDebuffs
     */
    private List<Status> mirroredBuffs;
    private List<Status> mirroredDebuffs;

    /**
     * Lists of statuses that will actually be displayed.
     */
    private List<Status> displayedBuffs;
    private List<Status> displayedDebuffs;
    
    private Map<Status, ScheduledFuture<?>> iconCancelTasks;

    private static int REMOVE_THRESHOLD_MIN = 150; // todo make configurable

    public Statuses() {
        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
        this.mirroredBuffs = new ArrayList<>();
        this.mirroredDebuffs = new ArrayList<>();
        this.iconCancelTasks = new HashMap<>();
    }
    // todo cancel cancel task
    public void add(Status status, boolean addIcon) {
        if (status.isHypixelDebuff()) {
            this.debuffs.add(status);
            if (addIcon) this.displayedDebuffs.add(status);
        } else {
            this.buffs.add(status);
            if (addIcon) this.displayedBuffs.add(status);
        }
    }
    public void removeHard(Status status, boolean removeIcon) {
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
     * duration on the status is <= REMOVE_THRESHOLD_MIN, it is not removed from the displayed list, and it will expire
     * on its own. Otherwise it is abruptly removed from the displayed list.
     * @param indexOnActionBar
     * @param isHypixelDebuff
     */
    public void removeSoft(int indexOnActionBar, boolean isHypixelDebuff) {
        Status removed;
        if (isHypixelDebuff) {
            removed = this.debuffs.remove(indexOnActionBar);
            if (removed.getRemainingDuration() > REMOVE_THRESHOLD_MIN) this.displayedDebuffs.remove(removed);
        } else {
            removed = this.buffs.remove(indexOnActionBar);
            if (removed.getRemainingDuration() > REMOVE_THRESHOLD_MIN) this.displayedBuffs.remove(removed);
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
