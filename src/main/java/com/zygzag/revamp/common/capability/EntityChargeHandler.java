package com.zygzag.revamp.common.capability;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.Arc;
import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.common.networking.packet.ClientboundEntityChargeSyncPacket;
import com.zygzag.revamp.common.registry.EnchantmentRegistry;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class EntityChargeHandler {
    public int ticksSinceLastModified = 500;
    public static final DamageSource SHOCKED_DAMAGE_SOURCE = new DamageSource("shocked");
    private final Entity entity;
    private float charge;
    private float maxCharge;
    public boolean dirty = true;

    public EntityChargeHandler(Entity entity, float charge, float maxCharge) {
        this.entity = entity;
        this.charge = charge;
        this.maxCharge = maxCharge;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getCharge() {
        return charge;
    }

    public float getMaxCharge() {
        return maxCharge;
    }

    public void setCharge(float c) {
        if (c != charge) {
            ticksSinceLastModified = 0;
            charge = c;
            markDirty();
        }
    }

    public void setMaxCharge(float c) {
        maxCharge = c;
        markDirty();
    }

    public void markDirty() {
        dirty = true;
    }
    public void markClean() {
        dirty = false;
    }

    int counter = 0;
    public void tick() {
        ticksSinceLastModified++;
        counter++;
        if (!entity.level.isClientSide) {
            if (Math.abs(charge) > maxCharge) {
                overload();
            }
            if (ticksSinceLastModified > 90) {
                float decayRate = Constants.CHARGE_DECAY_RATE;
                if (entity instanceof LivingEntity living) {
                    ItemStack feet = living.getItemBySlot(EquipmentSlot.FEET);
                    int lv = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.GROUNDED_ENCHANTMENT.get(), feet);
                    if (lv == 1) decayRate = Constants.GROUNDED_ENCHANTMENT_1_DECAY_RATE;
                    else if (lv == 2) decayRate = Constants.GROUNDED_ENCHANTMENT_2_DECAY_RATE;
                }
                if (Math.abs(charge) < decayRate) setCharge(0);
                else {
                    charge -= Math.signum(charge) * decayRate;
                    markDirty();
                }
            }
            if (Math.abs(charge) < Constants.EPSILON) setCharge(0);

            if (entity instanceof LivingEntity living) {
                ItemStack legs = living.getItemBySlot(EquipmentSlot.LEGS);
                int dynamoLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.DYNAMO_ENCHANTMENT.get(), legs);
                AttributeInstance speedInstance = living.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedInstance != null) {
                    if (speedInstance.getModifier(Constants.SPEED_MODIFIER_JOLTED_UUID) != null) {
                        speedInstance.removeModifier(Constants.SPEED_MODIFIER_JOLTED_UUID);
                    }
                    speedInstance.addTransientModifier(new AttributeModifier(Constants.SPEED_MODIFIER_JOLTED_UUID, "Jolted speed mod", dynamoLevel > 0 ? getCharge() * Constants.DYNAMO_ENCHANTMENT_SPEED_MULTIPLIER : getCharge() * Constants.CHARGE_SPEED_MULTIPLIER, AttributeModifier.Operation.ADDITION));
                }
                AttributeInstance attackSpeedInstance = living.getAttribute(Attributes.ATTACK_SPEED);
                if (attackSpeedInstance != null) {
                    if (attackSpeedInstance.getModifier(Constants.ATTACK_SPEED_MODIFIER_JOLTED_UUID) != null) {
                        attackSpeedInstance.removeModifier(Constants.ATTACK_SPEED_MODIFIER_JOLTED_UUID);
                    }
                    attackSpeedInstance.addTransientModifier(new AttributeModifier(Constants.ATTACK_SPEED_MODIFIER_JOLTED_UUID, "Jolted attack speed mod", getCharge() * Constants.CHARGE_ATTACK_SPEED_MULTIPLIER, AttributeModifier.Operation.ADDITION));
                }

                if (counter % 8 == 0) {
                    ItemStack chestplate = living.getItemBySlot(EquipmentSlot.CHEST);
                    int plusLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.VOLTAGE_PLUS_ENCHANTMENT.get(), chestplate);
                    int minusLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.VOLTAGE_MINUS_ENCHANTMENT.get(), chestplate);
                    float c = 0;
                    if (plusLevel > 0)
                        c += EnchantmentRegistry.VOLTAGE_PLUS_ENCHANTMENT.get().getChargePerTick() * 8;
                    if (minusLevel > 0)
                        c += EnchantmentRegistry.VOLTAGE_MINUS_ENCHANTMENT.get().getChargePerTick() * 8;
                    setCharge(getCharge() + c);
                }

                if (dynamoLevel > 0) {
                    Vec3 delta = new Vec3(living.xxa, 0, living.zza);
                    float speed = (float) delta.length();
                    //System.out.println(speed);
                    if (living.isOnGround()) {
                        if (charge / maxCharge - 2 / 3f < Constants.EPSILON) {
                            setCharge(Math.min(2 * getMaxCharge() / 3f, getCharge() + Constants.DYNAMO_ENCHANTMENT_DELTA_MULTIPLIER * speed));
                            AttributeInstance speedAttribute = living.getAttribute(Attributes.MOVEMENT_SPEED);
                            if (speedAttribute != null && speedAttribute.getModifier(Constants.SPEED_MODIFIER_DYNAMO_UUID) != null) speedAttribute.removeModifier(Constants.SPEED_MODIFIER_DYNAMO_UUID);
                        }
                        markDirty();
                    }
                } else {
                    AttributeInstance speedAttribute = living.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (speedAttribute != null && speedAttribute.getModifier(Constants.SPEED_MODIFIER_DYNAMO_UUID) != null) speedAttribute.removeModifier(Constants.SPEED_MODIFIER_DYNAMO_UUID);
                }
            }
            if (dirty) {
                ClientboundEntityChargeSyncPacket packet = new ClientboundEntityChargeSyncPacket(entity.getUUID(), getCharge(), getMaxCharge());
                if (entity instanceof ServerPlayer player)
                    RevampPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
                RevampPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
                markClean();
            }
        }
        if (Math.random() < Math.abs(charge) / 20) {
            Level world = entity.level;
            RandomSource rng = world.getRandom();
            Vec3 pos = GeneralUtil.randomPointOnAABB(entity.getBoundingBox(), rng).add(entity.position());
            world.addParticle(GeneralUtil.particle(getCharge()), pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }

    public void overload() {
        ItemStack helmet = ItemStack.EMPTY;
        Level world = entity.level;
        if (entity instanceof LivingEntity living) {
            helmet = living.getItemBySlot(EquipmentSlot.HEAD);
        }
        int level = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.SURGE_PROTECTOR_ENCHANTMENT.get(), helmet);
        float amountToChange = Math.min(Math.abs(charge) - getMaxCharge(), getMaxCharge());
        setCharge(getCharge() - Math.signum(charge) * amountToChange);
        if (level > 0) {
            if (!world.isClientSide) {
                List<Entity> entities = world.getEntities(entity, entity.getBoundingBox().inflate(Constants.ARC_RANGE));
                Entity entityNullable = GeneralUtil.minByOrNull(entities, (e) -> e.distanceToSqr(entity));
                GeneralUtil.ifNonNull(entityNullable, (target) -> {
                    new Arc(entity.position().add(0, entity.getBbHeight(), 0), target.getBoundingBox().getCenter(), (int) Math.abs(amountToChange)).sendToClients();
                    GeneralUtil.ifCapability(target, Revamp.ENTITY_CHARGE_CAPABILITY, (handler) ->
                        handler.setCharge(handler.getCharge() + amountToChange)
                    );
                });
            }
            entity.hurt(SHOCKED_DAMAGE_SOURCE, amountToChange * Constants.CHARGE_DAMAGE_MULTIPLIER * Constants.SURGE_PROTECTOR_DAMAGE_MULTIPLIER);
        } else {
            entity.hurt(SHOCKED_DAMAGE_SOURCE, amountToChange * Constants.CHARGE_DAMAGE_MULTIPLIER);
        }
    }
}
