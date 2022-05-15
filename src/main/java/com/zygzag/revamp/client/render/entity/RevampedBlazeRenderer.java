package com.zygzag.revamp.client.render.entity;

import com.zygzag.revamp.client.render.entity.model.RevampedBlazeModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.RevampedBlaze;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RevampedBlazeRenderer extends MobRenderer<RevampedBlaze, RevampedBlazeModel> {
    public static final ResourceLocation TEXTURE_LOC = Revamp.loc("textures/entity/revamped_blaze.png");

    public RevampedBlazeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RevampedBlazeModel(ctx.bakeLayer(RevampedBlazeModel.MAIN_LAYER)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(RevampedBlaze blaze) {
        return TEXTURE_LOC;
    }

    protected int getBlockLightLevel(RevampedBlaze blaze, BlockPos pos) {
        return 15;
    }
}
