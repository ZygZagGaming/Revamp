package com.zygzag.revamp.client.render;

import com.zygzag.revamp.client.render.entity.EmpoweredWitherRenderer;
import com.zygzag.revamp.client.render.entity.HomingWitherSkullRenderer;
import com.zygzag.revamp.client.render.entity.RevampedBlazeRenderer;
import com.zygzag.revamp.client.render.entity.ThrownAxeRenderer;
import com.zygzag.revamp.client.render.entity.model.EmpoweredWitherModel;
import com.zygzag.revamp.client.render.entity.model.RevampedBlazeModel;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.entity.RevampedBlaze;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
        event.registerEntityRenderer(Registry.EMPOWERED_WITHER.get(), EmpoweredWitherRenderer::new);
        event.registerEntityRenderer(Registry.REVAMPED_BLAZE.get(), RevampedBlazeRenderer::new);
        event.registerEntityRenderer(Registry.TRANSMUTATION_BOTTLE_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Registry.HOMING_WITHER_SKULL.get(), HomingWitherSkullRenderer::new);
        event.registerEntityRenderer(Registry.THROWN_AXE.get(), ThrownAxeRenderer::new);
    }
}
