package com.aquamancer.warlordsbufficons.handlers;

import com.aquamancer.warlordsbufficons.statuses.BuffEnum;
import com.aquamancer.warlordsbufficons.statuses.DebuffEnum;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ChatHandler {
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        // type == 0 means a message in chat
        if (event.type != 0 || !isGameMessage(message)) return;
        // get all the Buffs/Debuffs that the message was identified to be, as defined in ChatAbilityIdentifiers.
        // with the current Warlords on hypixel,
        // we expect there to be only zero or one matches.
        List<BuffEnum> buffMatches = ChatAbilityIdentifiers.getBuffMatches(message);
        List<DebuffEnum> debuffMatches = ChatAbilityIdentifiers.getDebuffMatches(message);
        if (buffMatches.size() + debuffMatches.size() <= 0) return; // no matches

//        for (BuffEnum buff : buffMatches)
//            StatusController.onChatBuff(buff);
//        for (DebuffEnum debuff : debuffMatches)
//            StatusController.onChatDebuff(debuff);
    }
    private static boolean isGameMessage(String s) {
        // todo
        return false;
    }
}
