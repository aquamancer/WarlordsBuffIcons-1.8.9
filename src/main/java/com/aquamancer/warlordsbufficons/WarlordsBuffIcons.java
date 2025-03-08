package com.aquamancer.warlordsbufficons;

import com.aquamancer.warlordsbufficons.chat.ChatAbilityIdentifiers;
import com.aquamancer.warlordsbufficons.graphics.IconRenderer;
import com.aquamancer.warlordsbufficons.handlers.RenderGameOverlayEventHandler;
import com.aquamancer.warlordsbufficons.statuses.StatusFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "warlordsbufficons", useMetadata=true)
public class WarlordsBuffIcons {
    public static boolean enabled;
    public static boolean chatDependencyEnabled;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new RenderGameOverlayEventHandler());
        FileManager.init();
        ChatAbilityIdentifiers.init();
        StatusFactory.init();
        IconRenderer.init();
        // todo for testing only
        RenderGameOverlayEventHandler.init();
    }
}