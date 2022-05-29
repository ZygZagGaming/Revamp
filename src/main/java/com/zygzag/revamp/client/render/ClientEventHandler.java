package com.zygzag.revamp.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.*;
import com.zygzag.revamp.common.entity.effect.SightEffect;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Revamp.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRender(RenderLevelLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack stack = event.getPoseStack();
        MultiBufferSource.BufferSource src = event.getLevelRenderer().renderBuffers.bufferSource();

        VertexConsumer buffer = src.getBuffer(Constants.TEST);
        Player player = mc.player;
        VoxelShape box = Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
        if (player != null) {
            Level world = player.level;

            stack.pushPose();
            int pbx = player.getBlockX();
            int pby = player.getBlockY();
            int pbz = player.getBlockZ();
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
            src.endBatch(Constants.TEST);
            stack.popPose();

            stack.pushPose();
            RenderType.lightning().setupRenderState();

            VertexConsumer consumer = src.getBuffer(RenderType.lightning());
            Vec3 offset = mc.gameRenderer.getMainCamera().getPosition();
            stack.translate(-offset.x, -offset.y, -offset.z);
            Matrix4f matr = stack.last().pose();
            GeneralUtil.ifCapability(world, Revamp.ARC_CAPABILITY, (handler) -> {
                for (Arc arc : handler.arcs) {
                    for (double s = (2 / 3.0); s >= 0.3; s -= (1 / 6.0)) {
                        List<Quad3D> borders = arc.getQuadsForRendering(s, arc.lifetime);
                        for (Quad3D quad : borders) {
                            quad(consumer, matr, quad, 0.45f, 0.45f, 0.5f, 0.3f);
                        }
                    }
                    //System.out.println("rendering arc with lifetime: " + arc.lifetime);
                }
            });

            RenderType.lightning().clearRenderState();
            src.endBatch(RenderType.lightning());
            stack.popPose();
        }

    }

    private static void quad(VertexConsumer consumer, Matrix4f matr, Vec3 a, Vec3 b, Vec3 c, Vec3 d, float r, float g, float b0, float a0) {
        // System.out.println("quad from: " + a + " to: " + b + " to: " + c + " to: " + d);
        vertex(consumer, matr, d, r, g, b0, a0);
        vertex(consumer, matr, c, r, g, b0, a0);
        vertex(consumer, matr, b, r, g, b0, a0);
        vertex(consumer, matr, a, r, g, b0, a0);
    }

    private static void quad(VertexConsumer consumer, Matrix4f matr, Quad3D quad, float r, float g, float b, float a) {
        quad(consumer, matr, quad.a(), quad.b(), quad.c(), quad.d(), r, g, b, a);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matr, Vec3 pos, float r, float g, float b, float a) {
        consumer.vertex(matr, (float) pos.x, (float) pos.y, (float) pos.z).color(r, g, b, a).endVertex();
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Level world = Minecraft.getInstance().level;
        if (world != null && !Minecraft.getInstance().isPaused()) {
            GeneralUtil.ifCapability(world, Revamp.CLIENT_LEVEL_CHARGE_CAPABILITY, ClientLevelChargeHandler::tick);
            GeneralUtil.ifCapability(world, Revamp.ARC_CAPABILITY, ArcHandler::tick);

            Iterable<Entity> entities = world.getEntities().getAll();
            for (Entity entity : entities) {
                GeneralUtil.ifCapability(entity, Revamp.ENTITY_CHARGE_CAPABILITY, EntityChargeHandler::tick);
            }
        }
    }
}
