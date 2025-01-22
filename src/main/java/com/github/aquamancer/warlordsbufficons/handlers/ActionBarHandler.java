package com.github.aquamancer.warlordsbufficons.handlers;

import com.github.aquamancer.warlordsbufficons.StatusController;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ActionBarHandler {
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (event.type != 2) return;
        StatusController.onActionBarChanged(event.message);
    }
}
