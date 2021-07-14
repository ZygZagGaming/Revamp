package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zygzag.revamp.client.model.DrifterEggModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.DrifterEggEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class DrifterEggRenderer extends MobRenderer<DrifterEggEntity, DrifterEggModel<DrifterEggEntity>> {
    private static ResourceLocation TEXTURE = new ResourceLocation(Revamp.MODID, "textures/entity/drifter_egg/drifter_egg.png");

    public DrifterEggRenderer(EntityRendererManager manager) {
        super(manager, new DrifterEggModel<>(), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(DrifterEggEntity p_110775_1_) {
        return TEXTURE;
    }

    @Override
    public void render(DrifterEggEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }


}
