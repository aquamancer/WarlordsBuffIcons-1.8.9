package com.aquamancer.warlordsbufficons;

import com.aquamancer.warlordsbufficons.chat.ChatUtils;
import com.aquamancer.warlordsbufficons.statuses.*;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;

public class StatusController {
    private static final Pattern WARLORDS_ACTIONBAR_IDENTIFIER = Pattern.compile(".*\\d+/\\d+.*");
    private static final Logger LOGGER = LogManager.getLogger(StatusController.class);
    
    private static final char DISPLAYED_BUFF_COLOR = 'a';
    private static final char DISPLAYED_DEBUFF_COLOR = 'c';

    /**
     * Represents previous raw actionbar data
     */
    private static ActionBarStatuses previousActionBar;
    private static Statuses statuses;
    
    public static void init() {
        previousActionBar = new ActionBarStatuses();
        statuses = new Statuses();
    }

    public static void onChatStatus(String universalName) {
        int duration = StatusFactory.calculateInitialDuration(universalName, statuses.getExperimentalInitialDurations());
        if (duration > 0)
            statuses.processNewPrematureStatus(StatusFactory.fromUniversalName(universalName, duration));
    }
    /*
        ACTION BAR METHODS -------------------------------------------------------
     */

    /**
     * Handles every action bar packet received.
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
        // [21:44:10] [Netty Client IO #6/INFO]: [net.minecraft.client.network.NetHandlerPlayClient:handler$zhl000$onChatPacketReceived:6496]:          §r          §6§lHP: §2§l4216§6§l/5571§r     §c§lRED Team§r    §aORBS§7:§610 §cWND§7:§63 §r§r
        // :handler$zhl000$onChatPacketReceived:6496]:               §r               §6§lHP: §e§l2370§6§l/6152§r     §9§lBLU Team§r    §aLINF§7:§61 §cCRIP§7:§63 §cWND§7:§63 §r§r
        List<Map.Entry<String, Integer>> buffs = new ArrayList<>();
        List<Map.Entry<String, Integer>> debuffs = new ArrayList<>();
        List<String> formattedStatuses = Arrays.stream(actionBar.getFormattedText().split(" "))
                .filter(s -> s.contains("§7:§6"))
                .collect(Collectors.toList());
        for (String formattedStatus : formattedStatuses) {
            List<Map.Entry<String, List<Character>>> components = new ArrayList<>();
            ChatUtils.parseFormattedChatMessage(formattedStatus, components);
            if (components.size() != 3) {
                LOGGER.error("parsing action bar substring containing §7:§6 resulted in size of {}. Expected: 3.\n{}", components.size(), actionBar.getFormattedText());
                return previousActionBar;
            }
            
            try {
                String statusName = components.get(0).getKey(); // 0 = name, 1 = :, 2 = duration
                Integer duration = Integer.parseInt(components.get(2).getKey());
                if (components.get(0).getValue().contains(DISPLAYED_DEBUFF_COLOR)) {
                    debuffs.add(new SimpleEntry<>(statusName, duration));
                } else {
                    buffs.add(new SimpleEntry<>(statusName, duration));
                }
            } catch (NumberFormatException ex) {
                LOGGER.error("couldn't parse {} to Integer while parsing action bar status substring", components.get(2).getKey());
                return previousActionBar;
            }
        }
        return new ActionBarStatuses(buffs, debuffs);
    }
    
    /**
     * Helper method to parse a single action bar status from string.
     * @param s
     * @return
     */
    private static Map.Entry<String, Map.Entry<Integer, Boolean>> parseActionBarStatus(String s) {
        boolean isDebuff;
        
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
