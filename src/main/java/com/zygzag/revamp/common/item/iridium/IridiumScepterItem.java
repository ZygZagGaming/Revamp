package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IridiumScepterItem extends Item implements ISocketable {
    Socket socket;
    public IridiumScepterItem(Properties properties, Socket socket) {
        super(properties);
        this.socket = socket;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        Socket socket = getSocket();
        AABB aabb;
        switch (socket) {
            case NONE -> {
                return InteractionResultHolder.fail(item);
            }
            case DIAMOND -> {
                aabb = new AABB(player.blockPosition().subtract(new Vec3i(20, 20, 20)), player.blockPosition().subtract(new Vec3i(-20, -20, -20)));
                List<ItemEntity> items = world.getEntities(EntityType.ITEM, aabb, (v) -> true);
                for (ItemEntity v : items) {
                    if (v.getItem().is(ItemTags.create(new ResourceLocation("revamp:diamond_scepter_consumable")))) {
                        int amount = v.getItem().getCount() / 8;
                        ExperienceOrb orb = new ExperienceOrb(world, v.getX(), v.getY(), v.getZ(), amount);
                        world.addFreshEntity(orb);
                        v.kill();
                    }
                }
            }
            case EMERALD -> {
                aabb = new AABB(player.blockPosition().subtract(new Vec3i(6, 3, 6)), player.blockPosition().subtract(new Vec3i(-6, -3, -6)));
                List<Villager> villagers = world.getEntities(EntityType.VILLAGER, aabb, (v) -> true);
                for (Villager v : villagers) {
                    MerchantOffers offers = v.getOffers();
                    for (int i = 0; i < offers.toArray().length; i++) {
                        MerchantOffer offer = offers.get(i);
                        while (offer.isOutOfStock()) {
                            offer.increaseUses();
                        }
                    }
                }
                ISocketable.addCooldown(player, item, Constants.EMERALD_SCEPTER_COOLDOWN);
            }
            case WITHER_SKULL -> {/* NEXT UPDATE:
                if (!world.isClientSide) {
                    ServerLevel level = (ServerLevel) world;
                    aabb = new AABB(player.blockPosition().subtract(new Vec3i(6, 3, 6)), player.blockPosition().subtract(new Vec3i(-6, -3, -6)));
                    List<Mob> undeads = level.getEntitiesOfClass(Mob.class, aabb, (m) -> m.getMobType() == MobType.UNDEAD);
                    for (Mob undead : undeads) {
                        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
                        lightning.setVisualOnly(true);
                        lightning.moveTo(undead.position());
                        level.addFreshEntity(lightning);
                        EntityType<?> type = undead.getType();
                        if (type == EntityType.ZOMBIE || type == EntityType.DROWNED || type == EntityType.HUSK || type == EntityType.ZOMBIFIED_PIGLIN || type == EntityType.PHANTOM || type == EntityType.HOGLIN) {
                            ItemStack stackRot = Registry.ROT_ESSENCE.get().getDefaultInstance();
                            stackRot.setCount((int) (Math.random() * 3) + 2);
                            ItemEntity entityRot = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), stackRot);
                            level.addFreshEntity(entityRot);
                            if (type == EntityType.HUSK) {
                                ItemStack sandStack = Registry.SAND_ESSENCE.get().getDefaultInstance();
                                sandStack.setCount((int) (Math.random() * 2));
                                ItemEntity sandEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), sandStack);
                                level.addFreshEntity(sandEntity);
                            } else if (type == EntityType.DROWNED) {
                                ItemStack waterStack = Registry.WATER_ESSENCE.get().getDefaultInstance();
                                waterStack.setCount((int) (Math.random() * 2));
                                ItemEntity waterEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), waterStack);
                                level.addFreshEntity(waterEntity);
                            } else if (type == EntityType.ZOMBIFIED_PIGLIN) {
                                ItemStack greedStack = Registry.GREED_ESSENCE.get().getDefaultInstance();
                                greedStack.setCount((int) (Math.random() * 2));
                                ItemEntity greedEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), greedStack);
                                level.addFreshEntity(greedEntity);
                            } else if (type == EntityType.PHANTOM) {
                                ItemStack nightmareStack = Registry.NIGHTMARE_ESSENCE.get().getDefaultInstance();
                                nightmareStack.setCount((int) (Math.random() * 2));
                                ItemEntity nightmareEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), nightmareStack);
                                level.addFreshEntity(nightmareEntity);
                            } else if (type == EntityType.HOGLIN) {
                                ItemStack feralStack = Registry.FERAL_ESSENCE.get().getDefaultInstance();
                                feralStack.setCount((int) (Math.random() * 2));
                                ItemEntity feralEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), feralStack);
                                level.addFreshEntity(feralEntity);
                            }
                        } else if (type == EntityType.SKELETON || type == EntityType.SKELETON_HORSE || type == EntityType.STRAY || type == EntityType.WITHER || type == EntityType.WITHER_SKELETON) {
                            ItemStack boneStack = Registry.BONE_ESSENCE.get().getDefaultInstance();
                            boneStack.setCount((int) (Math.random() * 3) + 2);
                            ItemEntity boneEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), boneStack);
                            level.addFreshEntity(boneEntity);
                            if (type == EntityType.SKELETON_HORSE) {
                                ItemStack allyshipStack = Registry.ALLYSHIP_ESSENCE.get().getDefaultInstance();
                                allyshipStack.setCount((int) (Math.random() * 2));
                                ItemEntity allyshipEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), allyshipStack);
                                level.addFreshEntity(allyshipEntity);
                            } else if (type == EntityType.STRAY) {
                                ItemStack iceStack = Registry.ICE_ESSENCE.get().getDefaultInstance();
                                iceStack.setCount((int) (Math.random() * 2));
                                ItemEntity iceEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), iceStack);
                                level.addFreshEntity(iceEntity);
                            } else if (type == EntityType.WITHER_SKELETON || type == EntityType.WITHER) {
                                ItemStack fireStack = Registry.FIRE_ESSENCE.get().getDefaultInstance();
                                ItemStack decayStack = Registry.DECAY_ESSENCE.get().getDefaultInstance();
                                fireStack.setCount((int) (Math.random() * 3) + 1);
                                decayStack.setCount((int) (Math.random() * 3) + 1);
                                ItemEntity fireEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), fireStack);
                                ItemEntity decayEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), decayStack);
                                level.addFreshEntity(fireEntity);
                                level.addFreshEntity(decayEntity);
                                if (type == EntityType.WITHER) {
                                    ItemStack powerStack = Registry.POWER_ESSENCE.get().getDefaultInstance();
                                    powerStack.setCount((int) (Math.random() * 2) + 1);
                                    ItemEntity powerEntity = new ItemEntity(level, undead.getX(), undead.getY(), undead.getZ(), powerStack);
                                    level.addFreshEntity(powerEntity);
                                }
                            }
                        }
                        undead.remove(Entity.RemovalReason.KILLED);
                    }
                    if (undeads.size() != 0) ISocketable.addCooldown(player, item, 1000);
                }*/
            }
            case AMETHYST -> {
                if (!player.getCooldowns().isOnCooldown(this)) {
                    aabb = new AABB(player.blockPosition().subtract(new Vec3i(40, 40, 40)), player.blockPosition().subtract(new Vec3i(-40, -40, -40)));
                    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, aabb, (v) -> true);
                    for (LivingEntity e : entities) {
                        e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200));
                        e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
                    }
                    ISocketable.addCooldown(player, item, Constants.AMETHYST_SCEPTER_COOLDOWN);
                }
            }
            case SKULL -> {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
                if (!world.isClientSide) {
                    ThrownPotion thrownpotion = new ThrownPotion(world, player);
                    ItemStack stack = Items.LINGERING_POTION.getDefaultInstance();
                    PotionUtils.setPotion(stack, Potions.LONG_POISON);
                    thrownpotion.setItem(stack);
                    thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
                    world.addFreshEntity(thrownpotion);
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                if (!player.getAbilities().instabuild) {
                    item.hurtAndBreak(4, player, (e) -> { });
                }
                ISocketable.addCooldown(player, item, Constants.SKULL_SCEPTER_COOLDOWN);
            }
        }
        return InteractionResultHolder.consume(item);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        Socket s = getSocket();
        Item i = s.i;
        MutableComponent m;
        if (s != Socket.NONE && world != null) {
            MutableComponent t = new TranslatableComponent("socketed.revamp").withStyle(ChatFormatting.GRAY);
            t.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
            t.append(((MutableComponent) i.getName(i.getDefaultInstance())).withStyle(ChatFormatting.GOLD));
            text.add(t);

            Socket socket = getSocket();
            text.add(new TextComponent(""));
            m = Minecraft.getInstance().options.keyUse.getKey().getDisplayName().copy().withStyle(ChatFormatting.GRAY);
            m.append(new TextComponent( ": ").withStyle(ChatFormatting.GRAY));
            m.append(new TranslatableComponent("use_ability.revamp.scepter." + socket.name().toLowerCase()).withStyle(ChatFormatting.GOLD));
            text.add(m);
            text.add(new TranslatableComponent("description.use_ability.revamp.scepter." + socket.name().toLowerCase()));
            if (hasCooldown()) {
                MutableComponent comp = new TranslatableComponent("revamp.cooldown").withStyle(ChatFormatting.GRAY);
                comp.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
                comp.append(new TextComponent(Float.toString(getCooldown(stack, world) / 20f) + " ").withStyle(ChatFormatting.GOLD));
                comp.append(new TranslatableComponent("revamp.seconds").withStyle(ChatFormatting.GRAY));
                text.add(comp);
            }
        }
    }

    @Override
    public boolean hasCooldown() {
        return socket != Socket.NONE && socket != Socket.DIAMOND;
    }

    @Override
    public boolean hasUseAbility() {
        return true;
    }

    @Override
    public int getCooldown(ItemStack stack, Level world) {
        int cooldownLevel = EnchantmentHelper.getItemEnchantmentLevel(Registry.EnchantmentRegistry.COOLDOWN_ENCHANTMENT.get(), stack);
        switch (socket) {
            case EMERALD -> {
                return Constants.EMERALD_SCEPTER_COOLDOWN / (cooldownLevel + 1);
            }
            case SKULL -> {
                return Constants.WITHER_SKULL_SWORD_COOLDOWN / (cooldownLevel + 1);
            }
            case WITHER_SKULL -> {
                return Constants.WITHER_SKULL_SCEPTER_COOLDOWN / (cooldownLevel + 1);
            }
            case AMETHYST -> {
                return Constants.AMETHYST_SCEPTER_COOLDOWN / (cooldownLevel + 1);
            }
        }
        return 0;
    }
}
