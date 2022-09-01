package com.zygzag.revamp.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.awt.*;

public class JoltedOverlay implements IGuiOverlay {
    public enum PosOrNeg {POSITIVE, NEGATIVE}
    public static ResourceLocation getLoc(int frame, PosOrNeg posOrNeg) {
        return Revamp.loc("textures/misc/jolted_" + posOrNeg.toString().toLowerCase() + "_" + frame + ".png");
    }

    public static final ResourceLocation GAUGE_BACKGROUND = Revamp.loc("textures/misc/gauge_background.png");
    public static final ResourceLocation GAUGE_DECALS = Revamp.loc("textures/misc/gauge_decals.png");
    public static final ResourceLocation GAUGE_FILL = Revamp.loc("textures/misc/gauge_fill.png");
    private int counter = 0;

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        Player player = Minecraft.getInstance().player;
        counter++;
        if (player != null) {
            GeneralUtil.ifCapability(player, Revamp.ENTITY_CHARGE_CAPABILITY, (handler) -> {
                float charge = handler.getCharge();
                if (charge > 0) {
                    renderTexture(getLoc((counter / Constants.JOLTED_FRAMETIME) % Constants.JOLTED_NUM_FRAMES, PosOrNeg.POSITIVE), charge / handler.getMaxCharge(), 0, 0, width, height);
                } else if (charge < 0) {
                    renderTexture(getLoc((counter / Constants.JOLTED_FRAMETIME) % Constants.JOLTED_NUM_FRAMES, PosOrNeg.NEGATIVE), -charge / handler.getMaxCharge(), 0, 0, width, height);
                }

                double scale = 1f * Minecraft.getInstance().getWindow().getGuiScale();
                int size = 32;
                double minX = 0;
                double maxY = height - 12;
                double maxX = minX + scale * size;
                double minY = maxY - scale * size;

                int t = handler.ticksSinceLastModified;
                float opacity = 1;
                if (t > 40 && charge == 0) opacity = Math.max((t - 160) / -120f, 0);
                //System.out.println("opacity: " + opacity + ", ticksSinceLastModified: " + t + " charge: " + charge);
                renderTexture(GAUGE_BACKGROUND, opacity, minX, minY, maxX, maxY);
                renderGaugeFill(scale, opacity, minX, minY, maxX, maxY, Math.min(charge / handler.getMaxCharge(), 1), counter);
                renderTexture(GAUGE_DECALS, opacity, minX, minY, maxX, maxY);
            });
        }
    }

    public static void renderGaugeFill(double scale, float opacity, double minX, double minY, double maxX, double maxY, float chargePercent, int counter) {
        maxX -= 8 * scale;
        minX += 8 * scale;
        Color color = getColor(chargePercent);
        ResourceLocation texture = getTexture(chargePercent, counter);
        double midY = (maxY + minY) / 2;
        double k = scale * 16 * chargePercent;
        if (chargePercent > 0) blit(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity, texture, minX, midY - k, maxX, midY, 0,1 - chargePercent, 1, 1);
        else blit(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity, texture, minX, midY, maxX, midY - k, 0, 1, 1, 1 + chargePercent);
    }


    public static ResourceLocation getTexture(float chargePercent, int counter) {
        if (Math.abs(chargePercent) < (2 / 3.0)) return GAUGE_FILL;
        else return Revamp.loc("textures/misc/gauge_fill_" + (counter / Constants.GAUGE_ANIM_FRAMETIME) % Constants.GAUGE_ANIM_NUM_FRAMES + ".png");
    }

    public static Color getColor(float chargePercent) {
        if (chargePercent > 0) {
            if (chargePercent > (1 / 3.0)) return new Color(Constants.HIGH_POSITIVE_CHARGE_COLOR);
            else return new Color(Constants.LOW_POSITIVE_CHARGE_COLOR);
        } else {
            if (chargePercent < -(1 / 3.0)) return new Color(Constants.HIGH_NEGATIVE_CHARGE_COLOR);
            else return new Color(Constants.LOW_NEGATIVE_CHARGE_COLOR);
        }
    }

    public static void renderTexture(ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(minX, maxY, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(maxX, maxY, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(maxX, minY, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(minX, minY, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void renderVerticallyFlippedTexture(float r, float g, float b, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(minX, maxY, -90.0D).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(maxX, maxY, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(maxX, minY, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(minX, minY, -90.0D).uv(0.0F, 1.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void renderTexture(Color color, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        renderTexture(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, texture, opacity, minX, minY, maxX, maxY);
    }

    public static void renderVerticallyFlippedTexture(Color color, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        renderVerticallyFlippedTexture(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, texture, opacity, minX, minY, maxX, maxY);
    }

    public static void renderTexture(float r, float g, float b, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(minX, maxY, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(maxX, maxY, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(maxX, minY, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(minX, minY, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void blit(float r, float g, float b, float opacity, ResourceLocation texture, double minX, double minY, double maxX, double maxY, float uvMinX, float uvMinY, float uvMaxX, float uvMaxY) {
        //System.out.println("blit params: rgb: " + r + " " + g + " " + b + ", opacity: " + opacity + ", texture: " + texture + ", minX: " + minX + ", minY: " + minY + ", maxX: " + maxX + ", maxY: " + maxY + ", uv: minX: " + uvMinX + ", minY: " + uvMinY + ", maxX: " + uvMaxX + ", maxY: " + uvMaxY);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(minX, maxY, -90.0D).uv(uvMinX, uvMaxY).endVertex();
        bufferbuilder.vertex(maxX, maxY, -90.0D).uv(uvMaxX, uvMaxY).endVertex();
        bufferbuilder.vertex(maxX, minY, -90.0D).uv(uvMaxX, uvMinY).endVertex();
        bufferbuilder.vertex(minX, minY, -90.0D).uv(uvMinX, uvMinY).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

