package com.zygzag.revamp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zygzag.revamp.client.render.entity.layer.EmpoweredWitherArmorLayer;
import com.zygzag.revamp.client.render.entity.model.EmpoweredWitherModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmpoweredWitherRenderer extends MobRenderer<EmpoweredWither, EmpoweredWitherModel<EmpoweredWither>> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = Revamp.loc("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_LOCATION = Revamp.loc("textures/entity/empowered_wither/empowered_wither.png");

    public EmpoweredWitherRenderer(EntityRendererProvider.Context context) {
        super(context, new EmpoweredWitherModel<>(context.bakeLayer(EmpoweredWitherModel.MAIN_LAYER)), 1.0F);
        //this.addLayer(new EmpoweredWitherArmorLayer(this, context.getModelSet()));
    }

    protected int getBlockLightLevel(EmpoweredWither wither, BlockPos pos) {
        return 15;
    }

    public ResourceLocation getTextureLocation(EmpoweredWither wither) {
        int i = wither.getInvulnerableTicks();
        return i > 0 && (i > 80 || i / 5 % 2 != 1) ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
    }

    protected void scale(EmpoweredWither wither, PoseStack stack, float fl) {
        float f = 2.0F;
        int i = wither.getInvulnerableTicks();
        if (i > 0) {
            f -= ((float)i - fl) / 220.0F * 0.5F;
        }

        stack.scale(f, f, f);
    }
}
