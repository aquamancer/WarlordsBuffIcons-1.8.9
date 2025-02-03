package com.aquamancer.warlordsbufficons.mixin;

import com.aquamancer.warlordsbufficons.StatusController;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    public void onChatPacketReceived(S02PacketChat packet, CallbackInfo ci) {
        if (packet.getType() == 2) {
            StatusController.onActionBarPacketReceived(packet.getChatComponent());
        }
    }
}
