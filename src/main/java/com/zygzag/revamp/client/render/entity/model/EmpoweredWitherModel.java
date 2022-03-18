package com.zygzag.revamp.client.render.entity.model;

// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EmpoweredWitherModel<T extends EmpoweredWither> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation MAIN_LAYER = new ModelLayerLocation(new ResourceLocation("revamp", "empowered_wither"), "main");
	private final ModelPart mainPart;
	private final ModelPart root;

	public EmpoweredWitherModel(ModelPart root) {
		this.root = root;
		this.mainPart = root.getChild("empowered_wither");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition empowered_wither = partdefinition.addOrReplaceChild("empowered_wither", CubeListBuilder.create(), PartPose.offset(-39.0F, 24.0F, 0.0F));

		PartDefinition left_head_r1 = empowered_wither.addOrReplaceChild("left_head_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-8.5F, -13.0F, -5.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-13.5F, -4.0F, -5.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 16).addBox(-8.5F, 7.0F, -5.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(39.0F, -15.5F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition main_body = empowered_wither.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -16.0F, -2.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(32, 10).addBox(-2.0F, -21.0F, -2.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(39.0F, 0.0F, 0.0F));

		PartDefinition left_shoulder_r1 = main_body.addOrReplaceChild("left_shoulder_r1", CubeListBuilder.create().texOffs(26, 31).addBox(-3.45F, -3.25F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.5F, -17.5F, -0.5F, 0.0F, 0.0F, 1.0472F));

		PartDefinition right_shoulder_r1 = main_body.addOrReplaceChild("right_shoulder_r1", CubeListBuilder.create().texOffs(14, 28).addBox(-1.5F, -3.5F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.2613F, -19.0365F, -0.5F, 0.0F, 0.0F, -1.0472F));

		PartDefinition collarbone_r1 = main_body.addOrReplaceChild("collarbone_r1", CubeListBuilder.create().texOffs(24, 16).addBox(-3.5F, -6.0F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -15.5F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition coccyx_r1 = main_body.addOrReplaceChild("coccyx_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -3.5F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6122F, 1.5212F, 0.7854F, 0.0F, 0.0F));

		PartDefinition ribs = main_body.addOrReplaceChild("ribs", CubeListBuilder.create(), PartPose.offset(5.5F, -14.0F, -0.5F));

		PartDefinition rib1 = ribs.addOrReplaceChild("rib1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = rib1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 6.0F, 5.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 1.5708F));

		PartDefinition cube_r2 = rib1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(36, 15).addBox(-3.5F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 2.5F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r3 = rib1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.25F, -2.35F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 1.5708F));

		PartDefinition rib2 = ribs.addOrReplaceChild("rib2", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 0.0F));

		PartDefinition cube_r4 = rib2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 6.0F, 5.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 1.5708F));

		PartDefinition cube_r5 = rib2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(36, 15).addBox(-3.5F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 2.5F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r6 = rib2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.25F, -2.35F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 1.5708F));

		PartDefinition rib3 = ribs.addOrReplaceChild("rib3", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 0.0F));

		PartDefinition cube_r7 = rib3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0).addBox(1.5F, 6.0F, 5.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 1.5708F));

		PartDefinition cube_r8 = rib3.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(36, 15).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 2.5F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r9 = rib3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(24, 0).addBox(1.5F, -1.25F, -2.35F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 1.5708F));

		PartDefinition rib4 = ribs.addOrReplaceChild("rib4", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 0.0F));

		PartDefinition cube_r10 = rib4.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 5).addBox(4.0F, 6.75F, 5.7F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 1.5708F));

		PartDefinition cube_r11 = rib4.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(36, 25).addBox(-1.0F, -4.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 5.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r12 = rib4.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(4, 5).addBox(4.0F, -1.0F, -2.05F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 1.5708F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { }

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		mainPart.render(poseStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public ModelPart root() {
		return root;
	}
}