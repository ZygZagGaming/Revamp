package com.zygzag.revamp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zygzag.revamp.common.entity.ThrownAxe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThrownAxeRenderer extends ThrownItemRenderer<ThrownAxe> {
    public static final ResourceLocation MODEL_LOCATION = new ResourceLocation("textures/entity/trident.png");

    public ThrownAxeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(ThrownAxe axe, float idk, float partialTick, PoseStack stack, MultiBufferSource sauce, int radius) {
        super.render(axe, idk, partialTick, stack, sauce, radius);
    }

    public ResourceLocation getTextureLocation(ThrownTrident p_116109_) {
        return MODEL_LOCATION;
    }
}
