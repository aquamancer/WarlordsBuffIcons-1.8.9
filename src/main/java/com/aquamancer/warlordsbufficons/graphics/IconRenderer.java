package com.aquamancer.warlordsbufficons.graphics;

import com.aquamancer.warlordsbufficons.io.FileManager;
import com.aquamancer.warlordsbufficons.statuses.PriorityLinkedList;
import com.aquamancer.warlordsbufficons.statuses.Status;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.List;

public class IconRenderer {
    public static boolean enabled = true;
    private static final Minecraft minecraft = Minecraft.getMinecraft();
    private static final Logger LOGGER = LogManager.getLogger(IconRenderer.class);
    private static GuiIngame gui;
    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final int DEFAULT_TEXTURE_SIZE = 256; // minecraft texture bind automatic rescaling px
    
    public static void render(PriorityLinkedList statuses, int x, int y, int iconWidth, int iconHeight, int maxWidth, int maxHeight) {
        if (!enabled) return;
        if (gui == null) gui = minecraft.ingameGUI;

        // todo handle if icons go off the screen?
        int maxCols = maxWidth / iconWidth;
        int maxRows = maxHeight / iconHeight;

        int i = 0;
        Iterator<Status> iterator = statuses.iterator();
        while (iterator.hasNext()) {
            Status status = iterator.next();
            int row = i / maxCols;
            int col = i % maxCols;
            int x1 = x + iconWidth * col; // columns stack to the right
            int y1 = y - iconHeight * row; // rows stack upwards
            if (i == maxCols * maxRows) {
                // todo finish this truncation (+x icon)
                return;
            } else if (i > maxCols * maxRows) {
                return;
            } else {
                int[] borderRGBA = status.getBorderRGBA();
                int[] handRGBA = FileManager.getHandRGBA();
                int[] iconOverlayRGBA = FileManager.getIconOverlayRGBA();
                drawScaledIcon2D(FileManager.getTextures().get(status.getUniversalName()), x1, y1, iconWidth, iconHeight);
                drawClockRect(x1, y1, iconWidth, iconHeight, status.getElapsed(), iconOverlayRGBA[0], iconOverlayRGBA[1], iconOverlayRGBA[2], iconOverlayRGBA[3], handRGBA[0], handRGBA[1], handRGBA[2], handRGBA[3]);
                drawBorder(x1, y1, iconWidth, iconHeight, borderRGBA[0], borderRGBA[1], borderRGBA[2], borderRGBA[3]);
            }
            i++;
        }
    }
    private static void drawScaledIcon2D(ResourceLocation icon, int x, int y, float width, float height) {
        if (icon == null) return;
        textureManager.bindTexture(icon);
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(width / DEFAULT_TEXTURE_SIZE, height / DEFAULT_TEXTURE_SIZE, 1);
//            gui.drawTexturedModalRect(scaledX, scaledY, 0, 0, iconWidth, iconHeight);
        gui.drawTexturedModalRect(0, 0, 0, 0, DEFAULT_TEXTURE_SIZE, DEFAULT_TEXTURE_SIZE);
        GlStateManager.popMatrix();
    }
    private static void drawBorder(double x, double y, double width, double height, int r, int g, int b, int a) {
        WorldRenderer wr = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.pushMatrix();
        wr.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        // draw border counter-clockwise, starting at the top left corner
        wr.pos(x, y, 0).color(r, g, b, a).endVertex();
        wr.pos(x, y + height, 0).color(r, g, b, a).endVertex();
        wr.pos(x + width, y + height, 0).color(r, g, b, a).endVertex();
        wr.pos(x + width, y, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }
    private static void drawClockRect(double x, double y, double width, double height, double elapsed, int r, int g, int b, int a, int r1, int g1, int b1, int a1) {
        double[] coordOfVertexOnBorder = new double[2];
        GlStateManager.disableTexture2D(); // otherwise breaks colors
        GlStateManager.pushMatrix();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
        // draw clock vertices counterclockwise, starting at the center of the rectangle
        double theta = 2 * Math.PI * elapsed;
        wr.pos(x + width / 2.0, y + height / 2.0, 0).color(r, g, b, a).endVertex();
        wr.pos(x + width / 2.0, y, 0).color(r, g, b, a).endVertex();
        if (elapsed > .875) {
            double phi = 2 * Math.PI - theta;
            double x1 = (width / 2) - (height / 2 * Math.tan(phi));
            coordOfVertexOnBorder[0] = x + x1;
            coordOfVertexOnBorder[1] = y;
            wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
        } else {
            wr.pos(x, y, 0).color(r, g, b, a).endVertex(); // top left corner
            if (elapsed > 0.75) {
                double phi = theta - 3 * Math.PI / 2;
                double y1 = (height / 2) - (width / 2 * Math.tan(phi));
                coordOfVertexOnBorder[0] = x;
                coordOfVertexOnBorder[1] = y + y1;
                wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
            } else if (elapsed > 0.625) {
                double phi = 3 * Math.PI / 2 - theta;
                double y2 = width / 2 * Math.tan(phi);
                coordOfVertexOnBorder[0] = x;
                coordOfVertexOnBorder[1] = y + height / 2 + y2;
                wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
            } else {
                wr.pos(x, y + height, 0).color(r, g, b, a).endVertex(); // bottom left corner
                if (elapsed > 0.5) {
                    double phi = theta - Math.PI;
                    double x1 = (width / 2) - (height / 2 * Math.tan(phi));
                    coordOfVertexOnBorder[0] = x + x1;
                    coordOfVertexOnBorder[1] = y + height;
                    wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
                } else if (elapsed > 0.375) {
                    double phi = Math.PI - theta;
                    double x1 = height / 2 * Math.tan(phi);
                    coordOfVertexOnBorder[0] = x + width / 2 + x1;
                    coordOfVertexOnBorder[1] = y + height;
                    wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
                } else {
                    wr.pos(x + width, y + height, 0).color(r, g, b, a).endVertex();
                    if (elapsed > 0.25) {
                        double phi = theta - Math.PI / 2;
                        double y1 = width / 2 * Math.tan(phi);
                        coordOfVertexOnBorder[0] = x + width;
                        coordOfVertexOnBorder[1] = y + height / 2 + y1;
                        wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
                    } else if (elapsed > 0.125) {
                        double phi = Math.PI / 2 - theta;
                        double y1 = width / 2 * Math.tan(phi);
                        coordOfVertexOnBorder[0] = x + width;
                        coordOfVertexOnBorder[1] = y + height / 2 - y1;
                        wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
                    } else {
                        wr.pos(x + width, y, 0).color(r, g, b, a).endVertex();
                        double x1 = height / 2 * Math.tan(theta);
                        coordOfVertexOnBorder[0] = x + width / 2 + x1;
                        coordOfVertexOnBorder[1] = y;
                        wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r, g, b, a).endVertex();
                    }
                }
            }
        }
        tessellator.draw();
        // draw clock hands
        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(x + width / 2, y, 0).color(r1, g1, b1, a1).endVertex();
        wr.pos(x + width / 2, y + height / 2, 0).color(r1, g1, b1, a1).endVertex();
        wr.pos(coordOfVertexOnBorder[0], coordOfVertexOnBorder[1], 0).color(r1, g1, b1, a1).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }
}
