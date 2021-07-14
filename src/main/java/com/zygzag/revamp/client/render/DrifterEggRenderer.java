package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.zygzag.revamp.client.model.DrifterEggModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.DrifterEggEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

public class DrifterEggRenderer extends MobRenderer<DrifterEggEntity, DrifterEggModel<DrifterEggEntity>> {
    private static ResourceLocation TEXTURE = new ResourceLocation(Revamp.MODID, "textures/entity/drifter_egg/drifter_egg.png");

    public DrifterEggRenderer(EntityRendererManager manager) {
        super(manager, new DrifterEggModel<>(), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(DrifterEggEntity drifterEgg) {
        return TEXTURE;
    }

    @Override
    public void render(DrifterEggEntity drifterEgg, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        super.render(drifterEgg, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }


}
