package com.zygzag.revamp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.GloidBubbleEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GloidBubbleRenderer extends EntityRenderer<GloidBubbleEntity> {
    public static final ResourceLocation TEXTURE_LOC_8 = Revamp.loc("textures/entity/gloid_bubble/gloid_bubble_8.png");
    public static final ResourceLocation TEXTURE_LOC_16 = Revamp.loc("textures/entity/gloid_bubble/gloid_bubble_16.png");
    public static final ResourceLocation TEXTURE_LOC_32 = Revamp.loc("textures/entity/gloid_bubble/gloid_bubble_32.png");
    public static final ResourceLocation TEXTURE_LOC_64 = Revamp.loc("textures/entity/gloid_bubble/gloid_bubble_64.png");
    public static final ResourceLocation[] TEXTURES = { TEXTURE_LOC_8, TEXTURE_LOC_16, TEXTURE_LOC_32, TEXTURE_LOC_64 };

    public GloidBubbleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(GloidBubbleEntity bubble) {
        return TEXTURES[(int) Math.min(Math.max(Math.round(Math.log(bubble.size()) / Math.log(2)) +  4, 3), 6) - 3];
    }

    protected int getBlockLightLevel(GloidBubbleEntity blaze, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(GloidBubbleEntity bubble, float n, float partialTicks, PoseStack stack, MultiBufferSource source, int light) {
        stack.pushPose();

        stack.translate(0, 0, 0);
        VertexConsumer vertexconsumer = source.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(bubble)));
        PoseStack.Pose posestack$pose = stack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        float age = (bubble.tickCount + partialTicks) / 5;
        float base = bubble.size(partialTicks) / 2;
        float xSize = base * (1 + 0.05f * Mth.sin(age + bubble.xWiggleOffset));
        float ySize = base * (1 + 0.05f * Mth.sin(age + bubble.yWiggleOffset));
        float zSize = base * (1 + 0.05f * Mth.sin(age + bubble.zWiggleOffset));

        quad(matrix4f, matrix3f, vertexconsumer, xSize, -ySize, zSize, xSize, -ySize, -zSize, -xSize, -ySize, -zSize, -xSize, -ySize, zSize, 0, 0, 1, 1, 0,  -1, 0, light); // bottom side
        quad(matrix4f, matrix3f, vertexconsumer, xSize, ySize, zSize, xSize, ySize, -zSize, -xSize, ySize, -zSize, -xSize, ySize, zSize, 0, 0, 1, 1, 0, 1, 0, light); // top side
        quad(matrix4f, matrix3f, vertexconsumer, -xSize, -ySize, zSize, -xSize, ySize, zSize, xSize, ySize, zSize, xSize, -ySize, zSize, 0, 0, 1, 1, 0, 0, 1, light);
        quad(matrix4f, matrix3f, vertexconsumer, -xSize, -ySize, -zSize, -xSize, ySize, -zSize, xSize, ySize, -zSize, xSize, -ySize, -zSize, 0, 0, 1, 1, 0, 0, 1, light);
        quad(matrix4f, matrix3f, vertexconsumer, xSize, -ySize, -zSize, xSize, ySize, -zSize, xSize, ySize, zSize, xSize, -ySize, zSize, 0, 0, 1, 1, 1, 0, 0, light);
        quad(matrix4f, matrix3f, vertexconsumer, -xSize, -ySize, -zSize, -xSize, ySize, -zSize, -xSize, ySize, zSize, -xSize, -ySize, zSize, 0, 0, 1, 1,  -1, 0, 0, light);

        stack.popPose();
        super.render(bubble, n, partialTicks, stack, source, light);
    }

    public void quad(Matrix4f matrix, Matrix3f normal, VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float uMin, float vMin, float uMax, float vMax, float nX, float nY, float nZ, int light) {
        vertex(matrix, normal, consumer, x1, y1, z1, uMin, vMin, nX, nY, nZ, light);
        vertex(matrix, normal, consumer, x2, y2, z2, uMin, vMax, nX, nY, nZ, light);
        vertex(matrix, normal, consumer, x3, y3, z3, uMax, vMax, nX, nY, nZ, light);
        vertex(matrix, normal, consumer, x4, y4, z4, uMax, vMin, nX, nY, nZ, light);
    }

    public void vertex(Matrix4f matrix, Matrix3f normal, VertexConsumer consumer, float x, float y, float z, float u, float v, float nX, float nZ, float nY, int light) {
        consumer.vertex(matrix, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(nX, nY, nZ).endVertex();
    }
}
