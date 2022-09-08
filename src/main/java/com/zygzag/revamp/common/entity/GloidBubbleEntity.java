package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.EntityRegistry;
import com.zygzag.revamp.common.registry.MobEffectRegistry;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GloidBubbleEntity extends Entity {
    public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(GloidBubbleEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> HITS = SynchedEntityData.defineId(GloidBubbleEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FORTIFIED = SynchedEntityData.defineId(GloidBubbleEntity.class, EntityDataSerializers.BOOLEAN);
    public float sizeO = 0;
    public static float PADDING_MULTIPLIER = 1.2f;
    public List<Entity> contained = new ArrayList<>();
    public List<Entity> containedOld = new ArrayList<>();
    public float xWiggleOffset = level.random.nextFloat(), yWiggleOffset = level.random.nextFloat(), zWiggleOffset = level.random.nextFloat();
    public static float BOAT_FRICTION = 0.05f / 0.9f;

    public GloidBubbleEntity(EntityType<?> type, Level world) {
        super(type, world);
        if (getVehicle() != null && !getVehicle().isRemoved()) surround(getVehicle());
    }

    public GloidBubbleEntity(Level world, Entity inside) {
        this(world, inside, false);
    }

    public GloidBubbleEntity(Level world, Entity inside, boolean isFortified) {
        super(EntityRegistry.GLOID_BUBBLE.get(), world);
        setFortified(isFortified);
        if (isFortified()) setHits(3);
        surround(inside);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SIZE, 0.3125f);
        entityData.define(HITS, 1);
        entityData.define(FORTIFIED, false);
    }

    public boolean isFortified() {
        return entityData.get(FORTIFIED);
    }

    public void setFortified(boolean n) {
        entityData.set(FORTIFIED, n);
    }

    public int hits() {
        return entityData.get(HITS);
    }

    public void setHits(int n) {
        entityData.set(HITS, n);
    }

    @Override
    public boolean hurt(DamageSource src, float amt) {
        setHits(hits() - 1);
        if (hits() <= 0) {
            pop(src);
        }
        return true;
    }

    public void pop(@Nullable DamageSource src) {
        Entity vehicle = getVehicle();
        if (vehicle != null) {
            vehicle.setNoGravity(false);
            if (vehicle instanceof Arrow arrow) {
                arrow.pickup = AbstractArrow.Pickup.ALLOWED; // TODO: make pickup save
            }
            if (vehicle instanceof LivingEntity living && src instanceof EntityDamageSource eds) {
                Entity a = eds.getEntity();
                if (a instanceof LivingEntity attacker && attacker != living) {
                    // copied code from Mob to calculate kb
                    float f1 = (float)attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + EnchantmentHelper.getKnockbackBonus(attacker) + 0.5f;
                    living.knockback(f1 * 0.7F, Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F)));
                }
            }
        }
        for (Entity e : contained) {
            e.setNoGravity(false); // just to be sure
        }
        discard();
        // TODO: some 'pop' animation + sound
    }

    public float size() {
        return entityData.get(SIZE);
    }

    public void setSize(float n) {
        entityData.set(SIZE, n);
    }

    public void surround(Entity entity) {
        setPos(entity.position());
        startRiding(entity);
        float size = entity instanceof ItemEntity ? 0.625f : Math.max(entity.dimensions.width * PADDING_MULTIPLIER, entity.dimensions.height * PADDING_MULTIPLIER);
        setSize(size);
        dimensions = EntityDimensions.fixed(size, size);
        remakeBoundingBox();
        if (entity instanceof Arrow arrow) {
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        }
    }

    public void remakeBoundingBox() {
        setBoundingBox(makeBoundingBox());
    }

    public AABB makeBoundingBox() {
        float size = size();
        return new AABB(position().x - size / 2, position().y - size / 2, position().z - size / 2, position().x + size / 2, position().y + size / 2, position().z + size / 2);
    }

    @Override
    public void tick() {
        super.tick();
        containedOld = contained;
        float size = size();
        if (sizeO != size) {
            sizeO = size;
            remakeBoundingBox();
        }
        if (getVehicle() == null || getVehicle().isRemoved()) {
            if (size() > 0.01f) {
                setSize(size() / (isFortified() ? 1.05f : 1.5f));
            } else {
                pop(null);
            }
        }
        contained = level.getEntities(this, getBoundingBox(), (it) -> !(it instanceof GloidBubbleEntity) && GeneralUtil.fullyContains(getBoundingBox(), it.getBoundingBox()));
        for (Entity e : contained) {
            if (e instanceof LivingEntity le) {
                le.addEffect(new MobEffectInstance(MobEffectRegistry.WEIGHTLESSNESS_EFFECT.get(), 2, 0, false, false));
            } else if (e instanceof Boat boat) {
                if (boat.status == Boat.Status.IN_AIR) {
                    Vec3 k = boat.getDeltaMovement().add(0, -BOAT_FRICTION, 0);
                    k = new Vec3(k.x(), Math.max(0, k.y()), k.z());
                    boat.setDeltaMovement(k);
                }
            }
            if (!containedOld.contains(e)) e.setNoGravity(true); // just to be sure
        }
        for (Entity e : containedOld) {
            if (!contained.contains(e) && e != getVehicle() && e.getPassengers().stream().noneMatch((it) -> it instanceof GloidBubbleEntity)) {
                GloidBubbleEntity offshoot = new GloidBubbleEntity(level, e);
                level.addFreshEntity(offshoot);
                Vec3 rand = GeneralUtil.randVectorNormalized(level.random);
                rand = new Vec3(rand.x(), Math.abs(rand.y()) + 1 + level.random.nextFloat(), rand.z()).scale(0.1);
                e.setDeltaMovement(e.getDeltaMovement().add(rand));
            }
        }
    }

    public float size(float partialTicks) {
        return partialTicks * size() + (1 - partialTicks) * sizeO;
    }

    @Override
    public double getMyRidingOffset() {
        Entity vehicle = getVehicle();
        return vehicle != null ? (vehicle instanceof ItemEntity ? 0.125 + GeneralUtil.bobHeight((ItemEntity) vehicle) : 0.0) - vehicle.getPassengersRidingOffset() + vehicle.dimensions.height / 2: 0.0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setSize(tag.getFloat("size"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("size", size());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
