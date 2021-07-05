package com.zygzag.revamp.event;

import com.zygzag.revamp.Registry;
import com.zygzag.revamp.Revamp;
import com.zygzag.revamp.util.EmpowermentData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Revamp.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void biomeLoadingEvent(BiomeLoadingEvent event) {
        if (!event.getCategory().equals(Biome.Category.NETHER) && !event.getCategory().equals(Biome.Category.THEEND)) {
            generateOre(event.getGeneration(), OreFeatureConfig.FillerBlockType.NATURAL_STONE, Registry.IRIDIUM_ORE.get().defaultBlockState(), 4, 0, 15, 1);
        }
    }

    public static void generateOre(BiomeGenerationSettingsBuilder settings, RuleTest test, BlockState state, int size, int min, int max, int count) {
        settings.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.configured(new OreFeatureConfig(test, state, size)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(min, 0, max)).squared().range(count)));
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        Item item = event.getItemStack().getItem();
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity) player;
            RayTraceResult result = sPlayer.pick(20.0, 0.0f, false);
            if (item.equals(Registry.SHULKER_BOWL.get())) {
                int length = event.getPlayer().getTicksUsingItem();
                System.out.println(length);
                if (length >= 32) {
                    event.getPlayer().setItemInHand(event.getHand(), event.getItemStack().copy());
                }
            } else if (result instanceof EntityRayTraceResult) {
                EntityRayTraceResult eResult = (EntityRayTraceResult) result;
                for (EmpowermentData i : Revamp.EMPOWERMENT_DATA_LIST) {

                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent event) {
        DamageSource src = event.getSource();
        if (src instanceof IndirectEntityDamageSource && src.getEntity() instanceof WitherSkullEntity) {
            if (src.getDirectEntity() instanceof WitherEntity) {
                WitherEntity wither = (WitherEntity) src.getDirectEntity();
                if (wither.hasEffect(Registry.EMPOWERMENT.get())) {
                    EffectInstance inst = wither.getEffect(Registry.EMPOWERMENT.get());
                    if (inst == null) throw new NullPointerException("sussy baka");
                    int amplifier = inst.getAmplifier() + 1;
                    for (EmpowermentData data : Revamp.EMPOWERMENT_DATA_LIST) {
                        if (data.entity == wither.getType()) {
                            // NYI
                        }
                    }
                }
            }
        }
    }

}
