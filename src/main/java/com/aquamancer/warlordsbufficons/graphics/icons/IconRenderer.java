package com.aquamancer.warlordsbufficons.graphics.icons;

import com.aquamancer.warlordsbufficons.statuses.Status;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class IconRenderer {
    private static final GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static final Runtime runtime = Runtime.getRuntime();
    public static void render(List<Status> statuses, int x, int y, int iconWidth, int iconHeight, int maxWidth, int maxHeight) {
        
    }
    public static void test() {
//        textureManager.bindTexture(new ResourceLocation("minecraft", "textures/items/apple_golden.png"));
//        gui.drawTexturedModalRect(50, 50, 0, 0, 250, 250);
        textureManager.bindTexture(new ResourceLocation("warlordsbufficons-1.8.9", "textures/gui/league-of-legends/Gangplank_Parrrley_HD.png"));
        GlStateManager.pushMatrix();
        GlStateManager.scale(10/256f, 10/256f, 1);
        gui.drawTexturedModalRect(150, 50, 0, 0, 96, 96);
        GlStateManager.popMatrix();
//        textureManager.bindTexture(new ResourceLocation("warlordsbufficons-1.8.9", "textures/gui/league-of-legends/240px-Fizz_Chum_the_Waters_HD.png"));
        GlStateManager.pushMatrix();
        GlStateManager.scale(64/240f, 64/240f, 1);
//        gui.drawTexturedModalRect(300, 50, 0, 0, 240, 240);
        GlStateManager.popMatrix();
        drawClockRect(100, 10, 100, 100, 0.1, 150);
        drawClockRect(210, 10, 100, 100, 0.2, 150);
        drawClockRect(320, 10, 100, 100, 0.3, 150);
        drawClockRect(430, 10, 100, 100, 0.45, 150);
        drawClockRect(540, 10, 100, 100, 0.6, 150);
        drawClockRect(100, 120, 100, 100, 0.7, 150);
        drawClockRect(210, 120, 100, 100, 0.8, 150);
        drawClockRect(320, 120, 100, 100, 0.9, 150);
        drawClockRect(430, 120, 100, 100, 1, 150);
//        gui.getChatGUI().printChatMessage(new ChatComponentText(Long.toString(runtime.freeMemory())));
//        gui.drawString(Minecraft.getMinecraft().fontRendererObj, "A;LSKALKSDFFADSAFD;AD;SFJLKSDFA", 100, 100, 0xFFFFFF);
    }
    public static void drawClockRect(double x, double y, double width, double height, double elapsed, int alpha) {
        GlStateManager.pushMatrix();
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
        // draw clock vertices counterclockwise, starting at the center of the rectangle
        double theta = 2 * Math.PI * elapsed;
        wr.pos(x + width / 2.0, y + height / 2.0, 0).color(0, 0, 0, alpha).endVertex();
        wr.pos(x + width / 2.0, y, 0).color(0, 0, 0, alpha).endVertex();
        if (elapsed > .875) {
            double phi = 2 * Math.PI - theta;
            double x1 = (width / 2) - (height / 2 * Math.tan(phi));
            wr.pos(x + x1, y, 0).color(0, 0, 0, alpha).endVertex();
        } else {
            wr.pos(x, y, 0).color(0, 0, 0, alpha).endVertex(); // top left corner
            if (elapsed > 0.75) {
                double phi = theta - 3 * Math.PI / 2;
                double y1 = (height / 2) - (width / 2 * Math.tan(phi));
                wr.pos(x, y + y1, 0).color(0, 0, 0, alpha).endVertex();
            } else if (elapsed > 0.625) {
                double phi = 3 * Math.PI / 2 - theta;
                double y2 = width / 2 * Math.tan(phi);
                wr.pos(x, y + height / 2 + y2, 0).color(0, 0, 0, alpha).endVertex();
            } else {
                wr.pos(x, y + height, 0).color(0, 0, 0, alpha).endVertex(); // bottom left corner
                if (elapsed > 0.5) {
                    double phi = theta - Math.PI;
                    double x1 = (width / 2) - (height / 2 * Math.tan(phi));
                    wr.pos(x + x1, y + height, 0).color(0, 0, 0, alpha).endVertex();
                } else if (elapsed > 0.375) {
                    double phi = Math.PI - theta;
                    double x1 = height / 2 * Math.tan(phi);
                    wr.pos(x + width / 2 + x1, y + height, 0).color(0, 0, 0, alpha).endVertex();
                } else {
                    wr.pos(x + width, y + height, 0).color(0, 0, 0, alpha).endVertex();
                    if (elapsed > 0.25) {
                        double phi = theta - Math.PI / 2;
                        double y1 = width / 2 * Math.tan(phi);
                        wr.pos(x + width, y + height / 2 + y1, 0).color(0, 0, 0, alpha).endVertex();
                    } else if (elapsed > 0.125) {
                        double phi = Math.PI / 2 - theta;
                        double y1 = width / 2 * Math.tan(phi);
                        wr.pos(x + width, y + height / 2 - y1, 0).color(0, 0, 0, alpha).endVertex();
                    } else {
                        wr.pos(x + width, y, 0).color(0, 0, 0, alpha).endVertex();
                        double x1 = height / 2 * Math.tan(theta);
                        wr.pos(x + width / 2 + x1, y, 0).color(0, 0, 0, alpha).endVertex();
                    }
                }
            }
        }
        tess.draw();
        GlStateManager.popMatrix();
    }
}
