package com.zygzag.revamp.client.model.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zygzag.revamp.client.model.DrifterEggModel;
import com.zygzag.revamp.common.entity.DrifterEggEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrifterEggShellLayer<T extends DrifterEggEntity> extends LayerRenderer<T, DrifterEggModel<T>> {
    private final EntityModel<T> model = new DrifterEggModel<>();

    public DrifterEggShellLayer(IEntityRenderer<T, DrifterEggModel<T>> renderer) {
        super(renderer);
    }

    public void render(MatrixStack stack, IRenderTypeBuffer buf, int packedLight, T entity, float limbSwing, float limbSwingAmount, float p_225628_7_, float age, float headYaw, float headPitch) {
        if (!entity.isInvisible()) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, p_225628_7_);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch);
            IVertexBuilder ivertexbuilder = buf.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
            this.model.renderToBuffer(stack, ivertexbuilder, packedLight, LivingRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}