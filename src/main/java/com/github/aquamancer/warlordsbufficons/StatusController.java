package com.github.aquamancer.warlordsbufficons;

import com.github.aquamancer.warlordsbufficons.statuses.BuffEnum;
import com.github.aquamancer.warlordsbufficons.statuses.DebuffEnum;
import com.github.aquamancer.warlordsbufficons.statuses.StackingStatus;
import com.github.aquamancer.warlordsbufficons.statuses.Status;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StatusController {
    private static List<Status> statuses = new ArrayList<>();
    private static void addStatus(Status status) {
        statuses.add(new StackingStatus());
    }
    public static void onChatBuff(BuffEnum buff) {

    }
    public static void onChatDebuff(DebuffEnum debuff) {
        
    }
    /*
        ACTION BAR METHODS -------------------------------------------------------
     */
    public static void onActionBarChanged(IChatComponent bar) {
        String message = bar.getUnformattedText();
        // verify the action bar message is warlords in game action bar, not something else, like +5 coins
        // this regex finds the hp.
        if (!Pattern.compile(".*\\d+/\\d+.*").matcher(message).find()) return;
        // for each buff that is in the same place, if the displayed duration changed, update the current duration

    }
}
