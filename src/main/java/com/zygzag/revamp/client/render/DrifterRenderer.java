package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zygzag.revamp.client.model.DrifterEggModel;
import com.zygzag.revamp.client.model.DrifterModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.DrifterEggEntity;
import com.zygzag.revamp.common.entity.DrifterEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class DrifterRenderer extends MobRenderer<DrifterEntity, DrifterModel<DrifterEntity>> {

    public DrifterRenderer(EntityRendererManager manager) {
        super(manager, new DrifterModel<>(), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(DrifterEntity drifter) {
        String texture = "";
        switch (drifter.age){
            case 0:
                texture = "textures/entity/drifter/drifter_larva.png";
            case 2:
                texture = "textures/entity/drifter/drifter_baby.png";
            case 3:
                texture = "textures/entity/drifter/drifter_adult.png";

        }
        return new ResourceLocation(Revamp.MODID, texture);
    }

    @Override
    public void render(DrifterEntity drifter, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        super.render(drifter, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }


}