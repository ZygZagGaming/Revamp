package com.zygzag.revamp.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zygzag.revamp.common.entity.DrifterEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DrifterModel <T extends DrifterEntity> extends EntityModel<T> {

    private final ModelRenderer larvaBody;
    private final ModelRenderer larvaTail;

    public DrifterModel(){
        super(RenderType::entityTranslucent);
        texWidth = 32;
        texHeight = 32;

        larvaBody = new ModelRenderer(this);
        larvaBody.setPos(0.0F, 21.0F, 0.0F);
        larvaBody.texOffs(0, 12).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

        larvaTail = new ModelRenderer(this);
        larvaTail.setPos(0.0F, 0.0F, 0.0F);
        larvaBody.addChild(larvaTail);
        larvaTail.texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.larvaBody.zRot = entity.yRot * ((float)Math.PI / 180F);
        this.larvaBody.xRot = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        larvaBody.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
