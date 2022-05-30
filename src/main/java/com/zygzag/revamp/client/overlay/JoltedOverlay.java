package com.zygzag.revamp.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.awt.*;

public class JoltedOverlay implements IIngameOverlay {
    public enum PosOrNeg {POSITIVE, NEGATIVE}
    public static ResourceLocation getLoc(int frame, PosOrNeg posOrNeg) {
        return Revamp.loc("textures/misc/jolted_" + posOrNeg.toString().toLowerCase() + "_" + frame + ".png");
    }
    public static final ResourceLocation GAUGE_BACKGROUND = Revamp.loc("textures/misc/gauge_background.png");
    public static final ResourceLocation GAUGE_DECALS = Revamp.loc("textures/misc/gauge_decals.png");
    public static final ResourceLocation GAUGE_FILL = Revamp.loc("textures/misc/gauge_fill.png");
    private int counter = 0;

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
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
                double midY = (minY + maxY) / 2;

                int t = handler.ticksSinceLastModified;
                float opacity = 1;
                if (t > 40 && charge == 0) opacity = Math.max((t - 160) / -120f, 0);
                //System.out.println("opacity: " + opacity + ", ticksSinceLastModified: " + t + " charge: " + charge);
                renderTexture(GAUGE_BACKGROUND, opacity, minX, minY, maxX, maxY);
                double h = Math.abs(scale * 16 * charge / handler.getMaxCharge());
                if (charge < 0)
                    renderVerticallyFlippedTexture(
                            getColor(charge / handler.getMaxCharge()),
                            getTexture(charge / handler.getMaxCharge(), counter),
                            opacity,
                            minX, midY - h, maxX, midY + h
                    );
                else
                    renderTexture(
                            getColor(charge / handler.getMaxCharge()),
                            getTexture(charge / handler.getMaxCharge(), counter),
                            opacity,
                            minX, midY - h, maxX, midY + h
                    );
                renderTexture(GAUGE_DECALS, opacity, minX, minY, maxX, maxY);
                //renderColor(getColor(charge / handler.getMaxCharge()), opacity, minX, minY, maxX, maxY);
            });
        }
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

    public void renderTexture(ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
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

    public void renderVerticallyFlippedTexture(float r, float g, float b, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
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

    public void renderTexture(Color color, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        renderTexture(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, texture, opacity, minX, minY, maxX, maxY);
    }

    public void renderVerticallyFlippedTexture(Color color, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
        renderVerticallyFlippedTexture(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, texture, opacity, minX, minY, maxX, maxY);
    }
    public void renderTexture(float r, float g, float b, ResourceLocation texture, float opacity, double minX, double minY, double maxX, double maxY) {
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
}
