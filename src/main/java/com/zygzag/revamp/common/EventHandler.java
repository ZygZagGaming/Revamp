package com.zygzag.revamp.common;

import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.common.item.iridium.IridiumChestplateItem;
import com.zygzag.revamp.common.item.iridium.IridiumShovelItem;
import com.zygzag.revamp.common.item.iridium.Socket;
import com.zygzag.revamp.common.item.recipe.EmpowermentRecipe;
import com.zygzag.revamp.common.item.recipe.ItemAndEntityHolder;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import net.minecraftforge.event.TickEvent;
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
        BiomeGenerationSettingsBuilder settings = event.getGeneration();
        if (!event.getCategory().equals(Biome.BiomeCategory.NETHER) && !event.getCategory().equals(Biome.BiomeCategory.THEEND)) {
            settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Registry.IRIDIUM_SMALL_PLACED.getHolder().get());
            settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Registry.IRIDIUM_LARGE_PLACED.getHolder().get());
            settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Registry.IRIDIUM_BURIED_PLACED.getHolder().get());
        }
    }

    @SubscribeEvent
    public static void onHurt(final LivingDamageEvent evt) {
        LivingEntity entity = evt.getEntityLiving();
        Level world = entity.level;
        long time = world.dayTime();
        DamageSource source = evt.getSource();
        float amt = evt.getAmount();
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.getItem() == Registry.DIAMOND_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
            AABB box = entity.getBoundingBox().inflate(16.0);
            Object[] blocks = world.getBlockStates(box).toArray();
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

        if (source.getEntity() != null) {
            Entity attacker = source.getEntity();

            if (attacker instanceof LivingEntity living) {
                ItemStack attackStack = living.getMainHandItem();
                Item item = attackStack.getItem();
                Item chestItem = living.getItemBySlot(EquipmentSlot.CHEST).getItem();

                if (stack.getItem() == Registry.WITHER_SKULL_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
                    int th = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.THORNS, stack);
                    MobEffectInstance effect = new MobEffectInstance(MobEffects.WITHER, 20 * (3 + th), th / 2);
                    living.addEffect(effect);
                }

                if (item == Registry.DIAMOND_SOCKETED_IRIDIUM_SWORD.get()) {
                    int height = attacker.getBlockY();
                    float damageBonus = (384f - height) / (112f/3f);
                    evt.setAmount(amt + damageBonus);
                } else if (item == Registry.EMERALD_SOCKETED_IRIDIUM_AXE.get() && entity.getType().is(RevampTags.ILLAGERS.get())) {
                    float damageBonus = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, attackStack) * 2.5f;
                    evt.setAmount(amt + damageBonus);
                } else if (item == Registry.SKULL_SOCKETED_IRIDIUM_SWORD.get()) {
                    float chance = 0.1f;
                    if (entity.getType().is(RevampTags.BOSSES.get())) chance = 0.025f;
                    else if (entity.getType() == EntityType.PLAYER) chance = 0.01f;
                    double rand = Math.random();
                    if (rand <= chance) {
                        evt.setAmount(Float.MAX_VALUE);
                    }
                } else if (item == Registry.SKULL_SOCKETED_IRIDIUM_HOE.get()) {
                    if (entity.getMobType() == MobType.UNDEAD) {
                        if (entity.getType().is(RevampTags.BOSSES.get())) evt.setAmount(25f);
                        else evt.setAmount(Float.MAX_VALUE);
                    }
                } else if (item == Registry.AMETHYST_SOCKETED_IRIDIUM_AXE.get()) {
                    if (time < 11834 || time > 22300) evt.setAmount(evt.getAmount() * 1.2f);
                } else if (item == Registry.AMETHYST_SOCKETED_IRIDIUM_SWORD.get()) {
                    if (time >= 11834 && time <= 22300) evt.setAmount(evt.getAmount() * 1.2f);
                    System.out.println(time);
                }

                if (chestItem == Registry.SKULL_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
                    float heal = amt / 4;
                    living.heal(heal);
                }
            }
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
        if (target instanceof Pillager pillager && (pillager.getHealth() <= 0 || pillager.isRemoved())) {
            AABB box = player.getBoundingBox().inflate(40d, 5d, 40d);
            List<IronGolem> golems = player.level.getEntitiesOfClass(IronGolem.class, box);
            for (IronGolem golem : golems) {
                for (MobEffectInstance effect : Registry.LONG_RAGE_POTION.get().getEffects()) {
                    golem.addEffect(effect);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        Item chestItem = chestStack.getItem();
        Level world = player.getLevel();
        if (chestItem instanceof IridiumChestplateItem plate) {
            Socket socket = plate.getSocket();
            if (socket == Socket.EMERALD) {
                MobEffectInstance effect = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
                if (effect == null || effect.getDuration() <= 5) {
                    player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 5, 0, true, false));
                }
            } else if (socket == Socket.AMETHYST) {
                MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
                if (effect == null || effect.getDuration() <= 5) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION));
                }
            }
        }
        ItemStack handStack = player.getMainHandItem();
        Item handItem = handStack.getItem();
        if (handItem instanceof IridiumShovelItem shovel) {
            if (shovel.getSocket() == Socket.SKULL && player.getCooldowns().isOnCooldown(shovel) && player.getCooldowns().getCooldownPercent(shovel, 0f) >= .95) {
                AABB box = player.getBoundingBox().inflate(1.5);
                List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, box);
                for (LivingEntity entity : list) {
                    if (entity != player) entity.hurt(DamageSource.playerAttack(player), 5f);
                }
            }
        }
    }
}























