package com.zygzag.revamp.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zygzag.revamp.entity.AbominationWitherEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;

public class AbominationWitherRenderer extends MobRenderer<AbominationWitherEntity, WitherModel<AbominationWitherEntity>> {
    private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

    public AbominationWitherRenderer(EntityRendererManager manager) {
        super(manager, new WitherModel<>(0.0F), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(AbominationWitherEntity entity) {
        return WITHER_LOCATION;
    }

    protected void scale(AbominationWitherEntity entity, MatrixStack stack, float scale) {
        float f = 2.0F;
        int i = entity.getInvulnerableTicks();
        if (i > 0) {
            f -= ((float)i - scale) / 220.0F * 0.5F;
        }

        stack.scale(f, f, f);
    }
}
