package com.aquamancer.warlordsbufficons;

import com.aquamancer.warlordsbufficons.statuses.*;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.regex.Pattern;

public class StatusController {
    private static final Pattern WARLORDS_ACTIONBAR_IDENTIFIER = Pattern.compile(".*\\d+/\\d+.*");
    /**
     * Used to compare if the recent action bar changed event indicates any changes to the buffs.<br>
     * 2d list of length 2 -> [list of buffs, list of debuffs].<br>
     * String = raw name/abbr of the buff as displayed in game.<br>
     * Integer = duration remaining
     */
    private static ActionBarStatuses previousActionBar;
    private static Statuses statuses;
    
    // todo make this configurable
    private static int KILL_DELAY_MILLIS = 1000;
    public static void init() {
        previousActionBar = new ActionBarStatuses();
        statuses = new Statuses();
    }

    public static void onChatStatus(Status status) {
    }
    /*
        ACTION BAR METHODS -------------------------------------------------------
     */

    /**
     * Hypixel Warlords action bar appends new buffs on the right of the action bar, with debuffs pinned
     * to the right.
     * @param bar
     */
    public static void onActionBarPacketReceived(IChatComponent bar) {
        String message = bar.getUnformattedText();
        // verify the action bar message is warlords in game action bar, not something else, like +5 coins
        if (!WARLORDS_ACTIONBAR_IDENTIFIER.matcher(message).find()) return;
        ActionBarStatuses currentActionBar = parseStatusesFromActionBar(bar);
        if (currentActionBar.equals(previousActionBar)) return;
        onActionBarStatusesChanged(currentActionBar);
        
        previousActionBar = currentActionBar;
    }

    private static ActionBarStatuses parseStatusesFromActionBar(IChatComponent actionBar) {
        // todo awaiting ingame testing for the format of the action bar string
        return null;
    }
    
    /**
     * Compares the previous raw action bar data to the recent raw action bar data. All found status additions are
     * added to statuses, and all status removals are removed from statuses. Statuses whose duration has changed
     * in this packet and are in the same index (after removals) will have their durations synced.
     * @param recentActionBar
     */
    // todo how do we handle status name not recognized. need to maintain mirrored order no matter what
    private static void onActionBarStatusesChanged(ActionBarStatuses recentActionBar) {
        Set<Integer> deletedBuffs = ActionBarStatuses.getDeletions(previousActionBar.getBuffs(), recentActionBar.getBuffs(), statuses.getMirroredBuffs());
        List<Map.Entry<String, Integer>> addedBuffs = ActionBarStatuses.getAdditions(previousActionBar.getBuffs(), recentActionBar.getBuffs(), deletedBuffs);
        Set<Integer> deletedDebuffs = ActionBarStatuses.getDeletions(previousActionBar.getDebuffs(), recentActionBar.getDebuffs(), statuses.getMirroredDebuffs());
        List<Map.Entry<String, Integer>> addedDebuffs = ActionBarStatuses.getAdditions(previousActionBar.getDebuffs(), recentActionBar.getDebuffs(), deletedDebuffs);

        for (Integer deletedBuff : deletedBuffs) {
            statuses.remove(deletedBuff, false, true); // alternatively can remove softly
        }
        // sync the durations of existing statuses up to statuses that have just been added
        // master and mirrored statuses have not yet been updated with the new buffs, so its size will be <= recentActionBar
        for (int i = 0; i < statuses.getMirroredBuffs().size(); i++) {
            statuses.getMirroredBuffs().get(i).sync(recentActionBar.getBuffs().get(i));
        }
        // add the new statuses
        statuses.processNewActionBarStatus(addedBuffs, false);
        
        for (Integer deletedDebuff : deletedDebuffs) {
            statuses.remove(deletedDebuff, true, true);
        }
        for (int i = 0; i < statuses.getMirroredDebuffs().size(); i++) {
            statuses.getMirroredBuffs().get(i).sync(recentActionBar.getDebuffs().get(i));
        }
        statuses.processNewActionBarStatus(addedDebuffs, true);
        statuses.clearPrematureStatuses();
    }
}
