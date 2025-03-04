package com.aquamancer.warlordsbufficons.graphics.icons;

import com.aquamancer.warlordsbufficons.statuses.Status;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class IconRenderer {
    public static boolean enabled = true;
    private static final Minecraft minecraft = Minecraft.getMinecraft();
    private static final Logger LOGGER = LogManager.getLogger(IconRenderer.class);
    private static GuiIngame gui;
    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static SimpleReloadableResourceManager resourceManager;
    
    public static void init() {
        if (Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager) {
            resourceManager = (SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
            gui = minecraft.ingameGUI;
        } else {
            LOGGER.error("resource manager is not of type SimpleReloadableResourceManager");
            enabled = false;
        }
    }
    public static void render(List<Status> statuses, int x, int y, int iconWidth, int iconHeight, int maxWidth, int maxHeight) {
        if (!enabled) return;
//        int maxIconsHoriz =

    }
    public static void test(double elapsed) {
//        textureManager.bindTexture(new ResourceLocation("warlordsbufficons-1.8.9", "textures/gui/league-of-legends/Gangplank_Parrrley_HD.png"));
        ResourceLocation fizz = new ResourceLocation("warlordsbufficons-1.8.9", "textures/gui/Yone_Spirit_Cleave_HD.png");
        drawScaledIcon2D(fizz, 150f, 50f, 30, 30);
        drawClockRect(150, 50, 30, 30, elapsed, 0, 0, 0, 120, 255, 255, 255, 255);
        drawBorder(150, 50, 256, 256, 255, 0, 0, 255);
    }
    private static void drawScaledIcon2D(ResourceLocation texture, float x, float y, float width, float height) {
        if (gui == null) {
            LOGGER.error("gui is null");
            gui = minecraft.ingameGUI;
        }
        GlStateManager.enableTexture2D();
        // get dimensions of png
        try {
            BufferedImage icon = ImageIO.read(resourceManager.getResource(texture).getInputStream());
            int iconHeight = icon.getHeight();
            int iconWidth = icon.getWidth();
            float scaledHeight = height / iconHeight;
            float scaledWidth = width / iconWidth;
            float scaledX = x / scaledWidth;
            float scaledY = y / scaledHeight;

            textureManager.bindTexture(texture);
            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.scale(scaledWidth, scaledHeight, 0);
//            gui.drawTexturedModalRect(scaledX, scaledY, 0, 0, iconWidth, iconHeight);
            gui.drawTexturedModalRect((int) scaledX, (int) scaledY, 0, 0, iconWidth, iconHeight);
            GlStateManager.popMatrix();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
