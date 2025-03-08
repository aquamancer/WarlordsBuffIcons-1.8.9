package com.aquamancer.warlordsbufficons.handlers;

import com.aquamancer.warlordsbufficons.StatusController;
import com.aquamancer.warlordsbufficons.graphics.icons.IconRenderer;
import com.aquamancer.warlordsbufficons.statuses.Status;
import com.aquamancer.warlordsbufficons.statuses.StatusFactory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;


public class RenderGameOverlayEventHandler {
    private static long previousTimeMillis = -1;
    private static int max = 5000;
    private static List<Status> testStatuses;
    public static void init() {
        testStatuses = new ArrayList<>();

        testStatuses.add(StatusFactory.createStatusFromUniversalName("timeWarp", 200, 5));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 13000, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 1000, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 2500, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 3333, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 4000, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 4222, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 6000, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
        testStatuses.add(StatusFactory.createStatusFromActionBarName("CHAIN", 7033, 14));
    }
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            if (previousTimeMillis == -1) previousTimeMillis = Minecraft.getSystemTime();
            long currentTime = Minecraft.getSystemTime();
            int delta = (int) (currentTime - previousTimeMillis);
            for (Status status : testStatuses) {
                status.timePassed(delta);
                if (status.getRemainingDuration() <= 0) {
                    status.setRemainingDuration(status.getInitialDuration());
                }
            }
            IconRenderer.render(testStatuses, 150, 50, 20, 20, 150, 150);
            previousTimeMillis = currentTime;
        }
    }
}
