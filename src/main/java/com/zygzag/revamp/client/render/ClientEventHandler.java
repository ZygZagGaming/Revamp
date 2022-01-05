package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.effect.SightEffect;
import com.zygzag.revamp.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Revamp.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRender(RenderLevelLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        VertexConsumer buffer = event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(Constants.TEST);
        Player player = mc.player;
        VoxelShape box = Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
        if (player != null) {
            int pbx = player.getBlockX();
            int pby = player.getBlockY();
            int pbz = player.getBlockZ();
            Level world = player.level;
            Vec3 pos = mc.gameRenderer.getMainCamera().getPosition();
            stack.translate(-pos.x, -pos.y, -pos.z);
            Constants.TEST.setupRenderState();
            RenderSystem.disableDepthTest();
            for (Map.Entry<MobEffect, MobEffectInstance> entry : player.getActiveEffectsMap().entrySet()) {
                MobEffect effect = entry.getKey();
                MobEffectInstance inst = entry.getValue();
                if (effect instanceof SightEffect s) {
                    if (inst != null) {
                        int range = 20 * (inst.getAmplifier() + 1);
                        for (int x = pbx - range; x <= pbx + range; x++) {
                            for (int y = pby - range; y <= pby + range; y++) {
                                for (int z = pbz - range; z <= pbz + range; z++) {
                                    BlockPos bp = new BlockPos(x, y, z);
                                    BlockState state = world.getBlockState(bp);
                                    if (s.test(state)) {
                                        int colorToUse = s.color(state);
                                        String hex = Integer.toString(colorToUse, 16);
                                        if (colorToUse != 0) {
                                            float r = Integer.parseInt(hex.substring(0, 2), 16) / 255f;
                                            float g = Integer.parseInt(hex.substring(2, 4), 16) / 255f;
                                            float b = Integer.parseInt(hex.substring(4), 16) / 255f;
                                            LevelRenderer.renderShape(stack, buffer, box, x, y, z, r, g, b, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            RenderSystem.enableDepthTest();
            Constants.TEST.clearRenderState();
        }
        event.getLevelRenderer().renderBuffers.bufferSource().endBatch(Constants.TEST);
        stack.popPose();
    }
}
