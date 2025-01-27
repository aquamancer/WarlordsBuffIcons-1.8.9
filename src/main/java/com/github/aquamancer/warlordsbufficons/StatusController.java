package com.github.aquamancer.warlordsbufficons;

import com.github.aquamancer.warlordsbufficons.statuses.*;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
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
    
    private static ScheduledThreadPoolExecutor executor = null;
    // todo make this configurable
    private static int KILL_DELAY_MILLIS = 1000;
    public static void init() {
        previousActionBar = new ActionBarStatuses();
        statuses = new Statuses();
        // todo shut this down after game end
        executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2);
        executor.setRemoveOnCancelPolicy(true);
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
        statuses.add(status);
        if (status.actionBarName.equals("")) return; // buffs not represented by the action bar
        // todo make sure this doesn't create a null pointer
        statuses.getIconCancelTasks().put(status,
              executor.schedule(() -> {
                    statuses.remove(status);
                    statuses.getIconCancelTasks().remove(status);
              }, 
                        KILL_DELAY_MILLIS,
                        TimeUnit.MILLISECONDS));
    }
    /*
        ACTION BAR METHODS -------------------------------------------------------
     */

    /**
     * Hypixel Warlords action bar appends new buffs on the right of the action bar, with debuffs pinned
     * to the right.
     * @param bar
     */
    public static void onActionBarChanged(IChatComponent bar) {
        String message = bar.getUnformattedText();
        // verify the action bar message is warlords in game action bar, not something else, like +5 coins
        if (!WARLORDS_ACTIONBAR_IDENTIFIER.matcher(message).find()) return;
        ActionBarStatuses actionBarStatuses = parseStatusesFromActionBar(message);
        if (actionBarStatuses.equals(previousActionBar)) return;
        updateStatuses(actionBarStatuses);
        
        previousActionBar = actionBarStatuses;
    }

    private static ActionBarStatuses parseStatusesFromActionBar(String actionBarMessage) {
        // todo awaiting ingame testing for the format of the action bar string
        return null;
    }
    // todo awaiting test: order of multiple hypixel debuffs at the same time.
    private static void updateStatuses(ActionBarStatuses currentActionBar) {
        // update buffs
        // this int is used to track the first occurrence of a buff having a different name from the previous in the
        // same index
        int indexOfFirstNameDifference = 0;
        for (int i = 0; i < Math.min(currentActionBar.getBuffs().size(), previousActionBar.getBuffs().size()); i++) {
            String previousName = previousActionBar.getBuffs().get(i).getKey();
            Integer previousDuration = previousActionBar.getBuffs().get(i).getValue();
            String currentName = currentActionBar.getBuffs().get(i).getKey();
            Integer currentDuration = currentActionBar.getBuffs().get(i).getValue();
            if (!previousName.equals(currentName)) {
                indexOfFirstNameDifference = i;
                break;
            }
            // previousName == currentName
            if (!previousDuration.equals(currentDuration)) {
                long offset = statuses.getMirroredBuffs().get(i).sync(currentDuration);
                // if the displayed remaining duration is way too far behind the expected, treat this as a
                // new status
                if (offset < x) {
                    // treat as new status
                }
            }
        }
    }
}
