package com.aquamancer.warlordsbufficons.graphics.icons;

import com.aquamancer.warlordsbufficons.statuses.Status;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class IconRenderer {
    private static final GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static final Runtime runtime = Runtime.getRuntime();
    public static void render(List<Status> statuses, int x, int y, int iconWidth, int iconHeight, int maxWidth, int maxHeight) {
        
    }
    public static void test(double elapsed) {
        textureManager.bindTexture(new ResourceLocation("warlordsbufficons-1.8.9", "textures/gui/league-of-legends/Gangplank_Parrrley_HD.png"));
        GlStateManager.pushMatrix();
        GlStateManager.scale(256/96f,  256/96f, 0);
        gui.drawTexturedModalRect(150 * 96/256f, 50 * 96/256f, 0, 0, 96, 96);
        GlStateManager.popMatrix();
        drawClockRect(150, 50, 256, 256, elapsed, 0, 0, 0, 150);
    }
    public static void drawClockRect(double x, double y, double width, double height, double elapsed, int r, int g, int b, int a) {
        GlStateManager.pushMatrix();
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
        // draw clock vertices counterclockwise, starting at the center of the rectangle
        double theta = 2 * Math.PI * elapsed;
        wr.pos(x + width / 2.0, y + height / 2.0, 0).color(r, g, b, a).endVertex();
        wr.pos(x + width / 2.0, y, 0).color(r, g, b, a).endVertex();
        if (elapsed > .875) {
            double phi = 2 * Math.PI - theta;
            double x1 = (width / 2) - (height / 2 * Math.tan(phi));
            wr.pos(x + x1, y, 0).color(r, g, b, a).endVertex();
        } else {
            wr.pos(x, y, 0).color(r, g, b, a).endVertex(); // top left corner
            if (elapsed > 0.75) {
                double phi = theta - 3 * Math.PI / 2;
                double y1 = (height / 2) - (width / 2 * Math.tan(phi));
                wr.pos(x, y + y1, 0).color(r, g, b, a).endVertex();
            } else if (elapsed > 0.625) {
                double phi = 3 * Math.PI / 2 - theta;
                double y2 = width / 2 * Math.tan(phi);
                wr.pos(x, y + height / 2 + y2, 0).color(r, g, b, a).endVertex();
            } else {
                wr.pos(x, y + height, 0).color(r, g, b, a).endVertex(); // bottom left corner
                if (elapsed > 0.5) {
                    double phi = theta - Math.PI;
                    double x1 = (width / 2) - (height / 2 * Math.tan(phi));
                    wr.pos(x + x1, y + height, 0).color(r, g, b, a).endVertex();
                } else if (elapsed > 0.375) {
                    double phi = Math.PI - theta;
                    double x1 = height / 2 * Math.tan(phi);
                    wr.pos(x + width / 2 + x1, y + height, 0).color(r, g, b, a).endVertex();
                } else {
                    wr.pos(x + width, y + height, 0).color(r, g, b, a).endVertex();
                    if (elapsed > 0.25) {
                        double phi = theta - Math.PI / 2;
                        double y1 = width / 2 * Math.tan(phi);
                        wr.pos(x + width, y + height / 2 + y1, 0).color(r, g, b, a).endVertex();
                    } else if (elapsed > 0.125) {
                        double phi = Math.PI / 2 - theta;
                        double y1 = width / 2 * Math.tan(phi);
                        wr.pos(x + width, y + height / 2 - y1, 0).color(r, g, b, a).endVertex();
                    } else {
                        wr.pos(x + width, y, 0).color(r, g, b, a).endVertex();
                        double x1 = height / 2 * Math.tan(theta);
                        wr.pos(x + width / 2 + x1, y, 0).color(r, g, b, a).endVertex();
                    }
                }
            }
        }
        tess.draw();
        GlStateManager.popMatrix();
    }
}
