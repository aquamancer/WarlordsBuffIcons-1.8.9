package com.aquamancer.warlordsbufficons.graphics;

import com.aquamancer.warlordsbufficons.StatusController;
import com.aquamancer.warlordsbufficons.statuses.PriorityLinkedList;
import com.aquamancer.warlordsbufficons.statuses.Status;

import java.util.List;

public class IconController {
    public static void render() {
        PriorityLinkedList buffs = StatusController.getStatuses().getDisplayedBuffs();
        PriorityLinkedList debuffs = StatusController.getStatuses().getDisplayedDebuffs();

    }
}
