package com.aquamancer.warlordsbufficons.statuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class Statuses {
    /**
     * "master" list of statuses that the icons will depend on.
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
    private Map<Status, ScheduledFuture<?>> iconCancelTasks;


    public Statuses() {
        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
        this.mirroredBuffs = new ArrayList<>();
        this.mirroredDebuffs = new ArrayList<>();
        this.iconCancelTasks = new HashMap<>();
    }
    public void add(Status status) {
        if (status.isHypixelDebuff()) {
            this.debuffs.add(status);
        } else {
            this.buffs.add(status);
        }
    }
    public void remove(Status status) {
        this.buffs.remove(status);
        this.debuffs.remove(status);
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
