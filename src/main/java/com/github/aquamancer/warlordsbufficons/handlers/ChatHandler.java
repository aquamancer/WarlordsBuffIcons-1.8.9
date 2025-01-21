package com.github.aquamancer.warlordsbufficons.handlers;

import com.github.aquamancer.warlordsbufficons.statuses.Buff;
import com.github.aquamancer.warlordsbufficons.statuses.Debuff;
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
        // we expect there to be only zero or one matches. More than one match means something is really wrong with
        // the identifiers
        List<Buff> buffMatches = ChatAbilityIdentifiers.getBuffMatches(message);
        List<Debuff> debuffMatches = ChatAbilityIdentifiers.getDebuffMatches(message);
        if (buffMatches.size() + debuffMatches.size() <= 0) return; // no matches
        if (buffMatches.size() + debuffMatches.size() > 1) {
            // one message is not supposed to be identified to be more than one buff/debuff.
            // todo logger
        }
        
        if (buffMatches.size() == 1) {
            // process new buff
        } else { // debuffMatches must be 1
            // process new debuff
        }
    }
    private static boolean isGameMessage(String s) {
        // todo
        return false;
    }
}
