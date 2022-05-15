package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ThrownAxe extends AbstractArrow implements ItemSupplier {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    private ItemStack item;

    public ThrownAxe(EntityType<? extends ThrownAxe> type, Level world) {
        super(type, world);
        this.item = Registry.IridiumGearRegistry.IRIDIUM_AXE.get().getDefaultInstance();
    }

    public ThrownAxe(Level world, Player owner, ItemStack item) {
        super(Registry.EntityRegistry.THROWN_AXE.get(), owner, world);
        this.item = item.copy();
    }

    public ItemStack getItem() {
        return item;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ID_LOYALTY, (byte)0);
        entityData.define(ID_FOIL, false);
    }

    public void tick() {
        if (inGroundTime > 4) {
            dealtDamage = true;
        }

        Entity entity = getOwner();
        int i = entityData.get(ID_LOYALTY);
        if (i > 0 && (dealtDamage || isNoPhysics()) && entity != null) {
            if (!isAcceptibleReturnOwner()) {
                if (!level.isClientSide && pickup == AbstractArrow.Pickup.ALLOWED) {
                    spawnAtLocation(getPickupItem(), 0.1F);
                }

                discard();
            } else {
                setNoPhysics(true);
                Vec3 vec3 = entity.getEyePosition().subtract(position());
                setPosRaw(getX(), getY() + vec3.y * 0.015D * (double)i, getZ());
                if (level.isClientSide) {
                    yOld = getY();
                }

                double d0 = 0.05D * (double)i;
                setDeltaMovement(getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));
                if (clientSideReturnTridentTickCount == 0) {
                    playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++clientSideReturnTridentTickCount;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity entity = getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    protected ItemStack getPickupItem() {
        return item.copy();
    }

    public boolean isFoil() {
        return entityData.get(ID_FOIL);
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_37575_, Vec3 p_37576_) {
        return dealtDamage ? null : super.findHitEntity(p_37575_, p_37576_);
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 8.0F;
        if (entity instanceof LivingEntity livingentity) {
            f += EnchantmentHelper.getDamageBonus(item, livingentity.getMobType());
        }

        Entity entity1 = getOwner();
        DamageSource damagesource = DamageSource.trident(this, entity1 == null ? this : entity1);
        dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity livingentity1) {
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity1);
                }

                doPostHurtEffects(livingentity1);
            }
        }

        setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        if (level instanceof ServerLevel && level.isThundering() && isChanneling()) {
            BlockPos blockpos = entity.blockPosition();
            if (level.canSeeSky(blockpos)) {
                LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                assert lightningbolt != null;
                lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                lightningbolt.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer)entity1 : null);
                level.addFreshEntity(lightningbolt);
                soundevent = SoundEvents.TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        playSound(soundevent, f1, 1.0F);
    }

    public boolean isChanneling() {
        return EnchantmentHelper.hasChanneling(item);
    }

    protected boolean tryPickup(Player p_150196_) {
        return super.tryPickup(p_150196_) || isNoPhysics() && ownedBy(p_150196_) && p_150196_.getInventory().add(getPickupItem());
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public void playerTouch(Player p_37580_) {
        if (ownedBy(p_37580_) || getOwner() == null) {
            super.playerTouch(p_37580_);
        }

    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Item", 10)) {
            item = ItemStack.of(tag.getCompound("Item"));
        }

        dealtDamage = tag.getBoolean("DealtDamage");
        entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(item));
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Item", item.save(new CompoundTag()));
        tag.putBoolean("DealtDamage", dealtDamage);
    }

    public void tickDespawn() {
        int i = entityData.get(ID_LOYALTY);
        if (pickup != AbstractArrow.Pickup.ALLOWED || i <= 0) {
            super.tickDespawn();
        }
    }
}
