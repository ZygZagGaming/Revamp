package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zygzag.revamp.client.model.layer.DrifterEggShellLayer;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.DrifterEggEntity;
import com.zygzag.revamp.client.model.DrifterEggModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.util.ResourceLocation;

public class DrifterEggRenderer extends MobRenderer<DrifterEggEntity, DrifterEggModel<DrifterEggEntity>> {
    private static ResourceLocation TEXTURE = new ResourceLocation(Revamp.MODID, "textures/entity/drifter_egg/drifter_egg.png");

    public DrifterEggRenderer(EntityRendererManager manager) {
        super(manager, new DrifterEggModel<>(), 0.3f);
        this.addLayer(new DrifterEggShellLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(DrifterEggEntity p_110775_1_) {
        return TEXTURE;
    }
}
