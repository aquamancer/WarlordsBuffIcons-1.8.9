package com.aquamancer.warlordsbufficons;

import com.aquamancer.warlordsbufficons.statuses.ActionBarStatuses;
import com.aquamancer.warlordsbufficons.statuses.Status;
import com.aquamancer.warlordsbufficons.statuses.StatusFactory;
import com.aquamancer.warlordsbufficons.statuses.Statuses;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    /**
     * Called when the chat handler identifies and returns a Status. This method immediately adds the status to be 
     * displayed. If the status is not represented by the action bar, e.g. slows, the icon run its entire duration
     * no matter what (subject to change - can identify cleanses or something). However, if the status should be
     * represented by the action bar, e.g. time warp, a task to kill the icon is initialized, set to a certain delay.
     * This task will be canceled if the action bar represents the change within that delay.
     * We cancel the task in case the status was cleansed before the action bar reflected the change in chat.
     * There can be a delay of about 750ms from chat message to action bar reflecting the event.
     * @param status
     */
    public static void onChatStatus(Status status) {
        statuses.add(status, true); // todo check if config icon enabled = false
        if (status.getActionBarName().equals("")) return; // buffs not represented by the action bar
        statuses.add(StatusFactory.createStatus())
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
        onActionBarChanged(currentActionBar);
        
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
    private static void onActionBarChanged(ActionBarStatuses recentActionBar) {
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
        statuses.add(addedBuffs, false);
    }
    // todo awaiting test: order of multiple hypixel debuffs at the same time.

    /**
     * Based on the previous action bar and recent raw action bar data, parse and construct currentStatusBar. <br>
     * This entails assigning currentActionBar to a shallow copy of previousActionBar, then removing and adding
     * new statuses based on the result of parsing algorithm of the raw action bar data. 
     */
    private static void constructCurrentStatuses(List<Map.Entry<String, Integer>> rawActionBarData) {
    }
}
