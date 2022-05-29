package com.zygzag.revamp.client.render.entity.model;

import com.zygzag.revamp.common.entity.RevampedBlaze;
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
    public void setupAnim(RevampedBlaze blaze, float a, float b, float counter, float yRot, float xRot) {
        int numRods = blaze.numRods();
        Vec3 current = blaze.position();
        Vec3 old = new Vec3(blaze.xOld, blaze.yOld, blaze.zOld);
        // System.out.println("current: " + current + " old: " + old);
        Vec3 delta = old.subtract(current).scale(50.0);
        head.xRot = xRot * ((float)Math.PI / 180F);
        head.yRot = yRot * ((float)Math.PI / 180F);
        float d = 0;
        if (numRods <= 8) d = 8;
        if (numRods <= 4) d = 16;
        head.y = d;
        float k = counter * (float) Math.PI * -0.1f;
        int i = 0;
        while (i < numRods) {
            ModelPart rod = rods[i];
            if (i < 4) {
                rod.setPos((float) (Math.cos(k) * 9 + delta.x()), (float) (-4 + Math.cos(i * 2 + counter / 2) + d + delta.y()), (float) (Math.sin(k) * 9 + delta.z()));
            } else if (i < 8) {
                if (i == 4) k = counter * (float) Math.PI * 0.05f;
                rod.setPos((float) (Math.cos(k) * 8 + delta.x() * 2), (float) (4 + Math.cos(i * 2 + counter / 2) + d + delta.y() * 2), (float) (Math.sin(k) * 7 + delta.z() * 2));
            } else {
                if (i == 8) k = counter * (float) Math.PI * -0.025f;
                rod.setPos((float) (Math.cos(k) * 7 + delta.x() * 3), (float) (12 + Math.cos( i * 2 + counter / 2) + d + delta.y() * 3), (float) (Math.sin(k) * 5 + delta.z() * 3));
            }
            k += Math.PI / 2;
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
