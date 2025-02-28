package com.aquamancer.warlordsbufficons;

import com.aquamancer.warlordsbufficons.chat.ChatAbilityIdentifiers;
import com.aquamancer.warlordsbufficons.statuses.StatusFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "warlordsbufficons", useMetadata=true)
public class WarlordsBuffIcons {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FileManager.init();
        ChatAbilityIdentifiers.init();
        StatusFactory.init();
    }
}