package com.aquamancer.warlordsbufficons.mixin;

import com.aquamancer.warlordsbufficons.StatusController;
import com.aquamancer.warlordsbufficons.handlers.ChatAbilityIdentifiers;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    private static final Logger LOGGER = LogManager.getLogger(MixinNetHandlerPlayClient.class);
    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    public void onChatPacketReceived(S02PacketChat packet, CallbackInfo ci) {
        System.out.println(packet.getChatComponent().getFormattedText());
        System.out.println(packet.getChatComponent().getUnformattedText());
        System.out.println(packet.getChatComponent().getUnformattedTextForChat());

        IChatComponent component = packet.getChatComponent();
        if (packet.getType() == 0) {
            String unformatted = component.getUnformattedText();
            if (unformatted.charAt(0) == '»') {
                String statusMatch = ChatAbilityIdentifiers.getMatchSelf(unformatted);
                if (statusMatch != null) StatusController.onChatStatus(unformatted);
            } else if (unformatted.charAt(0) == '«') {
                String statusMatch = ChatAbilityIdentifiers.getMatchExternal(unformatted);
                if (statusMatch != null) StatusController.onChatStatus(unformatted);
            }
        } else if (packet.getType() == 2) {
            StatusController.onActionBarPacketReceived(packet.getChatComponent());
        }
    }
}