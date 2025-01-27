package com.github.aquamancer.warlordsbufficons.statuses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
