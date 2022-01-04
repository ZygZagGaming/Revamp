package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Revamp.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRender(RenderLevelLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack stack = event.getPoseStack();
        float partialTicks = event.getPartialTick();
        stack.pushPose();
        VertexConsumer buffer = event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(Constants.TEST);
        Player player = mc.player;
        VoxelShape box = Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
        if (player != null && player.hasEffect(Registry.SIGHT_EFFECT.get())) {
            int pbx = player.getBlockX();
            int pby = player.getBlockY();
            int pbz = player.getBlockZ();
            MobEffectInstance inst = player.getEffect(Registry.SIGHT_EFFECT.get());
            Level world = player.level;
            if (inst != null) {

                int range = 20 * (inst.getAmplifier() + 1);
                Vec3 pos = mc.gameRenderer.getMainCamera().getPosition();
                stack.translate(-pos.x, -pos.y, -pos.z);
                Constants.TEST.setupRenderState();
                RenderSystem.disableDepthTest();
                for (int x = pbx - range; x <= pbx + range; x++) {
                    for (int y = pby - range; y <= pby + range; y++) {
                        for (int z = pbz - range; z <= pbz + range; z++) {
                            BlockPos bp = new BlockPos(x, y, z);
                            BlockState state = world.getBlockState(bp);
                            if (state.is(Tags.Blocks.ORES)) {
                                LevelRenderer.renderShape(stack, buffer, box, x, y, z, 1, 1, 1, 1);
                            }
                        }
                    }
                }
                RenderSystem.enableDepthTest();
                Constants.TEST.clearRenderState();
            }
        }
        event.getLevelRenderer().renderBuffers.bufferSource().endBatch(Constants.TEST);
        stack.popPose();
    }
}
