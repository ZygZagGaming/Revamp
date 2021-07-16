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

    private final ModelRenderer babyBody;
    private final ModelRenderer babyLeftWing;
    private final ModelRenderer babyRightWing;
    private final ModelRenderer babyHead;
    private final ModelRenderer babyTail;

    private final ModelRenderer adultBody;
    private final ModelRenderer adultHead;
    private final ModelRenderer adultTail;
    private final ModelRenderer adultTailEnd;
    private final ModelRenderer adultLeftWing;
    private final ModelRenderer adultRightWing;

    public DrifterModel(){
/// super(RenderType::entityTranslucent);
        texWidth = 129;
        texHeight = 128;

        larvaBody = new ModelRenderer(this);
        larvaBody.setPos(0.0F, 21.0F, 0.0F);
        larvaBody.texOffs(96, 12).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        larvaTail = new ModelRenderer(this);
        larvaTail.setPos(0.0F, 0.0F, 0.0F);
        larvaBody.addChild(larvaTail);
        larvaTail.texOffs(96, 0).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 8.0F, 0.0F, false);

        babyBody = new ModelRenderer(this);
        babyBody.setPos(0.0F, 24.0F, 0.0F);
        babyBody.texOffs(72, 64).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 4.0F, 12.0F, 0.0F, false);
        babyLeftWing = new ModelRenderer(this);
        babyLeftWing.setPos(8.0F, -3.0F, 4.0F);
        babyBody.addChild(babyLeftWing);
        babyLeftWing.texOffs(72, 80).addBox(0.0F, -1.0F, -4.0F, 16.0F, 2.0F, 8.0F, 0.0F, false);
        babyRightWing = new ModelRenderer(this);
        babyRightWing.setPos(-8.0F, -3.0F, 4.0F);
        babyBody.addChild(babyRightWing);
        babyRightWing.texOffs(72, 80).addBox(-16.0F, -1.0F, -4.0F, 16.0F, 2.0F, 8.0F, 0.0F, false);
        babyHead = new ModelRenderer(this);
        babyHead.setPos(1.0F, -3.0F, 0.0F);
        babyBody.addChild(babyHead);
        babyHead.texOffs(72, 90).addBox(-4.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        babyTail = new ModelRenderer(this);
        babyTail.setPos(0.0F, -3.0F, 12.0F);
        babyBody.addChild(babyTail);
        babyTail.texOffs(92, 90).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 16.0F, 0.0F, false);

        adultBody = new ModelRenderer(this);
        adultBody.setPos(0.0F, 24.0F, 0.0F);
        adultBody.texOffs(0, 0).addBox(-12.0F, -9.0F, 0.0F, 24.0F, 8.0F, 20.0F, 0.0F, false);

        adultHead = new ModelRenderer(this);
        adultHead.setPos(0.0F, -5.0F, 0.0F);
        adultBody.addChild(adultHead);
        adultHead.texOffs(38, 44).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, 0.0F, false);

        adultTail = new ModelRenderer(this);
        adultTail.setPos(0.0F, -3.0F, 20.0F);
        adultBody.addChild(adultTail);
        adultTail.texOffs(0, 44).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 6.0F, 26.0F, 0.0F, false);

        adultTailEnd = new ModelRenderer(this);
        adultTailEnd.setPos(0.0F, 0.0F, 0.0F);
        adultTail.addChild(adultTailEnd);
        adultTailEnd.texOffs(0, 76).addBox(-3.0F, -5.0F, 26.0F, 6.0F, 6.0F, 26.0F, 0.0F, false);

        adultLeftWing = new ModelRenderer(this);
        adultLeftWing.setPos(12.0F, -5.0F, 8.0F);
        adultBody.addChild(adultLeftWing);
        adultLeftWing.texOffs(0, 28).addBox(0.0F, -2.0F, -6.0F, 32.0F, 4.0F, 12.0F, 0.0F, false);

        adultRightWing = new ModelRenderer(this);
        adultRightWing.setPos(-12.0F, -5.0F, 8.0F);
        adultBody.addChild(adultRightWing);
        adultRightWing.texOffs(0, 28).addBox(-32.0F, -2.0F, -6.0F, 32.0F, 4.0F, 12.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.larvaBody.zRot = entity.yRot * ((float)Math.PI / 180F);
        this.larvaBody.xRot = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        adultBody.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
