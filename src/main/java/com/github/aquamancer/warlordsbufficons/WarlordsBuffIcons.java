package com.github.aquamancer.warlordsbufficons;

import com.github.aquamancer.warlordsbufficons.handlers.ChatHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "warlordsbufficons", useMetadata=true)
public class WarlordsBuffIcons {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // todo register events
        MinecraftForge.EVENT_BUS.register(new ChatHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }
}