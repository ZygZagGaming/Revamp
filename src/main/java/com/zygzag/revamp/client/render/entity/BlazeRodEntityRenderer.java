package com.zygzag.revamp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.BlazeRodEntity;
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
public class BlazeRodEntityRenderer extends EntityRenderer<BlazeRodEntity> {
    public static final ResourceLocation TEXTURE_LOC = Revamp.loc("textures/entity/revamped_blaze.png");

    public BlazeRodEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(BlazeRodEntity blaze) {
        return TEXTURE_LOC;
    }

    protected int getBlockLightLevel(BlazeRodEntity blaze, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(BlazeRodEntity rod, float n, float partialTicks, PoseStack stack, MultiBufferSource source, int light) {
        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, rod.yRotO, rod.getYRot()) - 90.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, rod.xRotO, rod.getXRot())));

        stack.scale(0.05625F, 0.05625F, 0.05625F);
        VertexConsumer vertexconsumer = source.getBuffer(RenderType.entityCutout(this.getTextureLocation(rod)));
        PoseStack.Pose posestack$pose = stack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        float rodTop = 16f / 32;
        float rodBottom = 26f / 32;
        float rod1 = 0;
        float rod2 = 2f / 64;
        float rod3 = 4f / 64;
        float rod4 = 6f / 64;
        float rod5 = 8f / 64;
        float[] rodArray = { rod1, rod2, rod3, rod4, rod5 };
        float rodTop1 = 14f / 64;
        float rodTop2 = 16f / 64;
        float rodTop3 = 18f / 64;
        float rodTop4 = 2f / 32;

        this.vertex(matrix4f, matrix3f, vertexconsumer, -5, -1, -1, rodTop1, rodTop4, -1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexconsumer, -5, -1, 1, rodTop2, rodTop4, -1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexconsumer, -5, 1, 1, rodTop2, 0, -1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexconsumer, -5, 1, -1, rodTop1, 0, -1, 0, 0, light);

        this.vertex(matrix4f, matrix3f, vertexconsumer, 5, 1, -1, rodTop2, rodTop4, -1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 5, 1, 1, rodTop3, rodTop4, -1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 5, -1, 1, rodTop3, 0, -1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 5, -1, -1, rodTop2, 0, -1, 0, 0, light);

        for (int j = 0; j < 4; ++j) {
            stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.vertex(matrix4f, matrix3f, vertexconsumer, -5, -1, 1, rodArray[j], rodTop, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, vertexconsumer, 5, -1, 1, rodArray[j], rodBottom, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, vertexconsumer, 5, 1, 1, rodArray[j + 1], rodBottom, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, vertexconsumer, -5, 1, 1, rodArray[j + 1], rodTop, 0, 1, 0, light);
        }

        stack.popPose();
        super.render(rod, n, partialTicks, stack, source, light);
    }

    public void vertex(Matrix4f matrix, Matrix3f normal, VertexConsumer consumer, int x, int y, int z, float u, float v, int nX, int nZ, int nY, int light) {
        consumer.vertex(matrix, (float)x, (float)y, (float)z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, (float)nX, (float)nY, (float)nZ).endVertex();
    }
}
