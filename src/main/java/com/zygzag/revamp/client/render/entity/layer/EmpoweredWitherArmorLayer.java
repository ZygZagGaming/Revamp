package com.zygzag.revamp.client.render.entity.layer;

import com.zygzag.revamp.client.render.entity.model.EmpoweredWitherModel;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class EmpoweredWitherArmorLayer extends EnergySwirlLayer<EmpoweredWither, EmpoweredWitherModel<EmpoweredWither>> {
    private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
    private final EmpoweredWitherModel<EmpoweredWither> model;

    public EmpoweredWitherArmorLayer(RenderLayerParent<EmpoweredWither, EmpoweredWitherModel<EmpoweredWither>> parent, EntityModelSet set) {
        super(parent);
        this.model = new EmpoweredWitherModel<>(set.bakeLayer(ModelLayers.WITHER_ARMOR));
    }

    protected float xOffset(float angle) {
        return Mth.cos(angle * 0.02F) * 3.0F;
    }

    protected ResourceLocation getTextureLocation() {
        return WITHER_ARMOR_LOCATION;
    }

    protected EmpoweredWitherModel<EmpoweredWither> model() {
        return this.model;
    }
}
