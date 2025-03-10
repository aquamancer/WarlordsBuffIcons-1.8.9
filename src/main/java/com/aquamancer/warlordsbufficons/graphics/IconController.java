package com.aquamancer.warlordsbufficons.graphics;

import com.aquamancer.warlordsbufficons.StatusController;
import com.aquamancer.warlordsbufficons.statuses.Status;

import java.util.List;

public class IconController {
    public static void render() {
        List<Status> buffs = StatusController.getStatuses().getDisplayedBuffs();
        List<Status> debuffs = StatusController.getStatuses().getDisplayedDebuffs();
        
    }
}
