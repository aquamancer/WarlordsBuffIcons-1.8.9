package com.github.aquamancer.warlordsbufficons;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatHandler {
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        System.out.println(event.message);
    }
}
