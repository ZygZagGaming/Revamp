package com.zygzag.revamp.render;

import com.zygzag.revamp.entity.CustomIronGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.util.ResourceLocation;

public class CustomIronGolemRenderer extends MobRenderer<CustomIronGolemEntity, IronGolemModel<CustomIronGolemEntity>> {
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    public CustomIronGolemRenderer(EntityRendererManager manager, IronGolemModel<CustomIronGolemEntity> model, float shadowRadius) {
        super(manager, model, shadowRadius);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomIronGolemEntity entity) {
        return GOLEM_LOCATION;
    }
}
