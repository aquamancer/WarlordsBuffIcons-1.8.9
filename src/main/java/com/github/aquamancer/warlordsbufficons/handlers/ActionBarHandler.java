package com.github.aquamancer.warlordsbufficons.handlers;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ActionBarHandler {
    @SubscribeEvent
    public void onActionBarChanged(ClientChatReceivedEvent event) {
        if (event.type == 2) {
            // handle event
        }
    }
}
