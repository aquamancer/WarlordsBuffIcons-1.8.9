package com.aquamancer.warlordsbufficons.handlers;

import com.aquamancer.warlordsbufficons.StatusController;
import com.aquamancer.warlordsbufficons.graphics.icons.IconRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class RenderGameOverlayEventHandler {
    private static long previousTimeMillis = Minecraft.getSystemTime();
    private static int max = 5000;
    private static int current = 5000;
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            long currentTime = Minecraft.getSystemTime();
            int delta = (int) (currentTime - previousTimeMillis);
            current -= delta;
            double elapsed = (max - current) / (double) max;
            IconRenderer.test(elapsed);
            previousTimeMillis = currentTime;
            if (current < 0) {
                current = 5000;
            }
        }
    }
}
