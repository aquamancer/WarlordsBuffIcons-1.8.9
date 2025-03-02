package com.aquamancer.warlordsbufficons.handlers;

import com.aquamancer.warlordsbufficons.graphics.icons.IconRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class RenderGameOverlayEventHandler {
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
            IconRenderer.test();
    }
}
