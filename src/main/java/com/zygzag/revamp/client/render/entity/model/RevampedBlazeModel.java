package com.zygzag.revamp.client.render.entity.model;

import com.zygzag.revamp.common.entity.BlazeRodEntity;
import com.zygzag.revamp.common.entity.RevampedBlaze;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RevampedBlazeModel extends HierarchicalModel<RevampedBlaze> {
    public static final ModelLayerLocation MAIN_LAYER = new ModelLayerLocation(new ResourceLocation("revamp", "revamped_blaze"), "main");
    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart head;
    private final ModelPart[] rods;
    public RevampedBlazeModel(ModelPart root) {
        this.root = root;
        this.main = root.getChild("revamped_blaze");
        this.head = main.getChild("head");
        this.rods = new ModelPart[12];
        for (int i = 0; i < 12; i++) {
            rods[i] = main.getChild("rod" + i);
        }
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(RevampedBlaze blaze, float animSpeed, float animPos, float ageInTicks, float headYRot, float headXRot) {
        head.xRot = headXRot * ((float)Math.PI / 180F);
        head.yRot = headYRot * ((float)Math.PI / 180F);
    }

    @Override
    public void prepareMobModel(RevampedBlaze blaze, float animSpeed, float animPos, float partialTick) {
        int numRods = blaze.numRods();
        Vec3 blazePos = blaze.position(partialTick);
        float d = 0;
        if (numRods <= 8) d = 8;
        if (numRods <= 4) d = 16;
        head.y = d;
        int i = 0;
        while (i < numRods) {
            ModelPart rod = rods[i];
            BlazeRodEntity rodEntity = blaze.getRod(i);
            Vec3 rodPos = rodEntity.position(partialTick).subtract(blazePos);
            Vec3 pos = GeneralUtil.rotateAbout(rodPos.scale(16), Vec3.ZERO, Math.PI, (blaze.yBodyRot(partialTick) * Math.PI / 180)).add(-1, 16, -1);
            rod.setPos((float) pos.x, (float) pos.y, (float) pos.z);
            i++;
        }
        for (int j = numRods; j < 12; j++) {
            rods[j].setPos(0f, 1000f, 0f);
        }
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition partDef = meshDef.getRoot();
        PartDefinition revampedBlaze = partDef.addOrReplaceChild("revamped_blaze", CubeListBuilder.create(), PartPose.ZERO);
        revampedBlaze.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        CubeListBuilder rod = CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
        for (int i = 0; i < 12; i++) {
            revampedBlaze.addOrReplaceChild("rod" + i, rod, PartPose.offset(0f, 1000f, 0f));
        }
        return LayerDefinition.create(meshDef, 64, 32);
    }
}
