package com.zygzag.revamp.common;

import com.zygzag.revamp.common.capability.ChunkChargeHandler;
import com.zygzag.revamp.common.capability.EntityChargeHandler;
import com.zygzag.revamp.common.entity.GloidBubbleEntity;
import com.zygzag.revamp.common.entity.effect.WeightlessnessEffect;
import com.zygzag.revamp.common.item.iridium.IridiumChestplateItem;
import com.zygzag.revamp.common.item.iridium.IridiumShovelItem;
import com.zygzag.revamp.common.item.iridium.Socket;
import com.zygzag.revamp.common.item.recipe.EmpowermentRecipe;
import com.zygzag.revamp.common.item.recipe.ItemAndEntityHolder;
import com.zygzag.revamp.common.registry.IridiumGearRegistry;
import com.zygzag.revamp.common.registry.PotionRegistry;
import com.zygzag.revamp.common.registry.RecipeTypeRegistry;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = Revamp.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onHurt(final LivingDamageEvent evt) {
        LivingEntity entity = evt.getEntity();
        Level world = entity.level;
        long time = world.dayTime();
        DamageSource source = evt.getSource();
        float amt = evt.getAmount();
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.getItem() == IridiumGearRegistry.DIAMOND_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
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

                if (stack.getItem() == IridiumGearRegistry.WITHER_SKULL_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
                    int th = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.THORNS, stack);
                    MobEffectInstance effect = new MobEffectInstance(MobEffects.WITHER, 20 * (3 + th), th / 2);
                    living.addEffect(effect);
                }

                if (item == IridiumGearRegistry.DIAMOND_SOCKETED_IRIDIUM_SWORD.get()) {
                    int height = attacker.getBlockY();
                    float damageBonus = (384f - height) / (112f/3f);
                    evt.setAmount(amt + damageBonus);
                } else if (item == IridiumGearRegistry.EMERALD_SOCKETED_IRIDIUM_AXE.get() && entity.getType().is(RevampTags.ILLAGERS.get())) {
                    float damageBonus = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SMITE, attackStack) * 2.5f;
                    evt.setAmount(amt + damageBonus);
                } else if (item == IridiumGearRegistry.SKULL_SOCKETED_IRIDIUM_SWORD.get()) {
                    float chance = 0.1f;
                    if (entity.getType().is(RevampTags.BOSSES.get())) chance = 0.025f;
                    else if (entity.getType() == EntityType.PLAYER) chance = 0.01f;
                    double rand = Math.random();
                    if (rand <= chance) {
                        evt.setAmount(Float.MAX_VALUE);
                    }
                } else if (item == IridiumGearRegistry.SKULL_SOCKETED_IRIDIUM_HOE.get()) {
                    if (entity.getMobType() == MobType.UNDEAD) {
                        if (entity.getType().is(RevampTags.BOSSES.get())) evt.setAmount(25f);
                        else evt.setAmount(Float.MAX_VALUE);
                    }
                } else if (item == IridiumGearRegistry.AMETHYST_SOCKETED_IRIDIUM_AXE.get()) {
                    if (time < 11834 || time > 22300) evt.setAmount(evt.getAmount() * 1.2f);
                } else if (item == IridiumGearRegistry.AMETHYST_SOCKETED_IRIDIUM_SWORD.get()) {
                    if (time >= 11834 && time <= 22300) evt.setAmount(evt.getAmount() * 1.2f);
                }

                if (chestItem == IridiumGearRegistry.SKULL_SOCKETED_IRIDIUM_CHESTPLATE.get()) {
                    float heal = amt / 4;
                    living.heal(heal);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.EntityInteract evt) {
        ItemStack stack = evt.getItemStack();
        Level world = evt.getLevel();
        List<EmpowermentRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.EMPOWERMENT.get());
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
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (target instanceof Pillager pillager && (pillager.getHealth() <= 0 || pillager.isRemoved())) {
            AABB box = player.getBoundingBox().inflate(40d, 5d, 40d);
            List<IronGolem> golems = player.level.getEntitiesOfClass(IronGolem.class, box);
            for (IronGolem golem : golems) {
                for (MobEffectInstance effect : PotionRegistry.LONG_RAGE_POTION.get().getEffects()) {
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

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        List<Component> list = event.getToolTip();
        if (event.getItemStack().is(Items.SHULKER_SHELL)) list.add(Component.translatable("tooltip.revamp.shulker_shell_extra").withStyle(ChatFormatting.DARK_GRAY));
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        Level world = event.level;
        ChunkSource source = world.getChunkSource();
        if (source instanceof ServerChunkCache cache) {
            Iterable<ChunkHolder> chunks = cache.chunkMap.getChunks();
            for (ChunkHolder holder : chunks) {
                LevelChunk chunk = holder.getFullChunk();
                if (chunk != null) {
                    GeneralUtil.ifCapability(chunk, Revamp.CHUNK_CHARGE_CAPABILITY, ChunkChargeHandler::tick);
                }
            }
        }

        Iterable<Entity> entities = world.getEntities().getAll();
        for (Entity entity : entities) {
            GeneralUtil.ifCapability(entity, Revamp.ENTITY_CHARGE_CAPABILITY, EntityChargeHandler::tick);
        }

        GeneralUtil.ifCapability(world, Revamp.LEVEL_CONNECTION_TRACKER_CAPABILITY, (handler) -> {
            handler.connectionTrackers.forEach((tag, tracker) -> {
                tracker.printSections();
            });
        });
    }

    @SubscribeEvent
    public static void blockUpdate(BlockEvent.NeighborNotifyEvent event) {
        LevelAccessor accessor = event.getLevel();
        if (accessor instanceof Level world) {
            GeneralUtil.ifCapability(world, Revamp.LEVEL_CONNECTION_TRACKER_CAPABILITY, (handler) -> {
                handler.update(event.getPos());
            });
        }
    }

    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (event.getEffectInstance().getEffect() instanceof WeightlessnessEffect) entity.setNoGravity(true);
    }

    @SubscribeEvent
    public static void effectRemoved(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (event.getEffect() instanceof WeightlessnessEffect) entity.setNoGravity(false);
    }

    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance inst = event.getEffectInstance();
        if (inst != null && inst.getEffect() instanceof WeightlessnessEffect) entity.setNoGravity(false);
    }

    @SubscribeEvent
    public static void itemPickedUp(EntityItemPickupEvent event) {
        ItemEntity entity = event.getItem();
        if (entity.getPassengers().stream().anyMatch((it) -> it instanceof GloidBubbleEntity)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void xpOrbPickedUp(PlayerXpEvent.PickupXp event) {
        ExperienceOrb entity = event.getOrb();
        if (entity.getPassengers().stream().anyMatch((it) -> it instanceof GloidBubbleEntity)) event.setCanceled(true);
    }
}























