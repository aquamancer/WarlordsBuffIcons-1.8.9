//package com.github.aquamancer.warlordsbufficons.mixin;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiNewChat;
//import net.minecraft.client.network.NetHandlerPlayClient;
//import net.minecraft.network.play.server.S02PacketChat;
//import net.minecraft.util.ChatComponentText;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(NetHandlerPlayClient.class)
//public class MixinNetHandlerPlayClient {
//    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
//    public void onChatPacketReceived(S02PacketChat packet, CallbackInfo ci) {
//        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
//        chat.printChatMessage(new ChatComponentText("id:" + packet.getType()));
//        chat.printChatMessage(packet.getChatComponent());
//    }
//}
