package com.zygzag.revamp.common;

import com.zygzag.revamp.common.item.recipe.EmpowermentRecipe;
import com.zygzag.revamp.common.item.recipe.ItemAndEntityHolder;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.common.world.feature.PlacedFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = Revamp.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void biomeLoadingEvent(BiomeLoadingEvent event) {
        if (!event.getCategory().equals(Biome.BiomeCategory.NETHER) && !event.getCategory().equals(Biome.BiomeCategory.THEEND)) {
            BiomeGenerationSettingsBuilder settings = event.getGeneration();
            settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.IRIDIUM_SMALL.get());
            settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.IRIDIUM_LARGE.get());
            settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.IRIDIUM_BURIED.get());
        }
    }

    @SubscribeEvent
    public static void onAttack(final AttackEntityEvent evt) {
        Player player = evt.getPlayer();
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (item == Registry.DIAMOND_SOCKETED_IRIDIUM_SWORD.get()) {
            int height = player.getBlockY();
            float extraDamage = (-0.0375f * (float) height) + 9.6f;
            System.out.println(evt.getTarget().hurt(new EntityDamageSource("extra", player), extraDamage));
        }
    }

    @SubscribeEvent
    public static void onHurt(final LivingDamageEvent evt) {
        LivingEntity entity = evt.getEntityLiving();
        Level level = entity.level;
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.getItem() == Registry.DIAMOND_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
            AABB box = entity.getBoundingBox().inflate(16.0);
            Object[] blocks = level.getBlockStates(box).toArray();
            HashMap<Block, Integer> map = new HashMap<>();
            int extra = 0;
            for (Object obj : blocks) {
                if (obj instanceof BlockState state && state.is(Tags.Blocks.ORES)) {
                    Block block = state.getBlock();
                    if (map.containsKey(block)) {
                        int n = map.get(block);
                        if (n == 32) {
                            map.put(block, 1);
                            extra++;
                        } else map.put(block, map.get(block) + 1);
                    }
                    else map.put(block, 1);
                }
            }
            extra += map.size();
            evt.setAmount(Math.max(evt.getAmount() - ((float) extra * 0.25f), 0.05f / extra));
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.EntityInteract evt) {
        ItemStack stack = evt.getItemStack();
        Level world = evt.getWorld();
        List<EmpowermentRecipe> recipes = world.getRecipeManager().getAllRecipesFor(ModRecipeType.EMPOWERMENT);
        ItemAndEntityHolder holder = new ItemAndEntityHolder(stack, evt.getTarget());
        for (EmpowermentRecipe recipe : recipes) {
            if (recipe.matches(holder, world)) {
                recipe.assemble(holder);
                System.out.println(recipe.getId());
                evt.getTarget().remove(Entity.RemovalReason.CHANGED_DIMENSION);
                world.addFreshEntity(holder.entity);
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKill(final AttackEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getTarget();
        Level world = player.level;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (target instanceof Pillager pillager && (pillager.getHealth() <= 0 || pillager.isRemoved())) {
            AABB box = player.getBoundingBox().inflate(40d, 5d, 40d);
            List<IronGolem> golems = player.level.getEntitiesOfClass(IronGolem.class, box);
            for (IronGolem golem : golems) {
                for (MobEffectInstance effect : Registry.LONG_RAGE_POTION.get().getEffects()) {
                    golem.addEffect(effect);
                }
            }
        }

        if (stack.getItem() == Registry.EMERALD_SOCKETED_IRIDIUM_AXE.get()) {
            Tag<EntityType<?>> illagerTag = EntityTypeTags.getAllTags().getTag(new ResourceLocation("forge:illagers"));
            if (illagerTag != null && target.getType().is(illagerTag)) {
                int num = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, stack);
                if (num > 0) {
                    target.hurt(DamageSource.playerAttack(player), 2.5f * num);
                }
            }

            if (illagerTag == null) Revamp.LOGGER.warn("Illager tag forge:illagers is null, assuming intentional");
        } else if (stack.getItem() == Registry.SKULL_SOCKETED_IRIDIUM_SWORD.get()) {
            Tag<EntityType<?>> bossTag = EntityTypeTags.getAllTags().getTag(new ResourceLocation("forge:bosses"));
            double n = 0.1;
            if (bossTag != null && target.getType().is(bossTag)) n = 0.025;
            if (target.getType() == EntityType.PLAYER) n = 0.01;
            if (world.random.nextDouble() < n) {
                target.hurt(DamageSource.playerAttack(player), Float.MAX_VALUE);
                System.out.println("instakill");
            }

            if (bossTag == null) Revamp.LOGGER.warn("Boss tag forge:bosses is null, assuming intentional");
        }
    }
}
