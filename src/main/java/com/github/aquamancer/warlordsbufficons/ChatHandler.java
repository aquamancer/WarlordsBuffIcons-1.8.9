package com.github.aquamancer.warlordsbufficons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatHandler {
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        chat.printChatMessage(new ChatComponentText(event.message.toString()));
        chat.printChatMessage(new ChatComponentText("id: " + event.type));
    }
}
