package com.zygzag.revamp.client.render;

import com.zygzag.revamp.client.particle.ChargeParticle;
import com.zygzag.revamp.client.render.entity.*;
import com.zygzag.revamp.client.render.entity.model.EmpoweredWitherModel;
import com.zygzag.revamp.client.render.entity.model.RevampedBlazeModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.registry.EntityRegistry;
import com.zygzag.revamp.common.registry.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Revamp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EmpoweredWitherModel.MAIN_LAYER, EmpoweredWitherModel::createBodyLayer);
        event.registerLayerDefinition(RevampedBlazeModel.MAIN_LAYER, RevampedBlazeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.EMPOWERED_WITHER.get(), EmpoweredWitherRenderer::new);
        event.registerEntityRenderer(EntityRegistry.REVAMPED_BLAZE.get(), RevampedBlazeRenderer::new);
        event.registerEntityRenderer(EntityRegistry.TRANSMUTATION_BOTTLE_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HOMING_WITHER_SKULL.get(), HomingWitherSkullRenderer::new);
        event.registerEntityRenderer(EntityRegistry.THROWN_AXE.get(), ThrownAxeRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.register(ParticleTypeRegistry.CHARGE_PARTICLE_TYPE_POSITIVE.get(), ChargeParticle.Provider::new);
        event.register(ParticleTypeRegistry.CHARGE_PARTICLE_TYPE_NEGATIVE.get(), ChargeParticle.Provider::new);
    }
}
