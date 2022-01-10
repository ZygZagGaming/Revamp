package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.zygzag.revamp.common.entity.ThrownAxe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThrownAxeRenderer extends EntityRenderer<ThrownAxe> {
    public static final ResourceLocation LOC = new ResourceLocation("revamp:items/wither_skull_socketed_iridium_axe");
    private final ItemRenderer itemRenderer;

    public ThrownAxeRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
    }

    public void render(ThrownAxe axe, float a, float b, PoseStack stack, MultiBufferSource source, int shadowRadius) {
        stack.pushPose();
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        this.itemRenderer.renderStatic(axe.getItem(), ItemTransforms.TransformType.GROUND, shadowRadius, OverlayTexture.NO_OVERLAY, stack, source, axe.getId());
        stack.popPose();
        super.render(axe, a, b, stack, source, shadowRadius);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownAxe axe) {
        return LOC;
    }
}
