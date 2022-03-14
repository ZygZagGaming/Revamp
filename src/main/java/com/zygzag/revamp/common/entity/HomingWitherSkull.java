package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HomingWitherSkull extends WitherSkull {
    private static final EntityDataAccessor<Optional<UUID>> DATA_TARGET_UUID = SynchedEntityData.defineId(HomingWitherSkull.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_AUTO_TARGET = SynchedEntityData.defineId(HomingWitherSkull.class, EntityDataSerializers.BOOLEAN);
    Predicate<Entity> targetSelector = (e) -> e instanceof LivingEntity l && EmpoweredWither.LIVING_ENTITY_SELECTOR.test(l);
    double speed = 0.2;

    @Override
    public boolean isDangerous() {
        return false;
    }

    public HomingWitherSkull(EntityType<? extends HomingWitherSkull> type, Level world) {
        super(type, world);
    }

    public HomingWitherSkull(Level world, LivingEntity owner, double x, double y, double z) {
        super(Registry.HOMING_WITHER_SKULL.get(), world);
        setOwner(owner);
        this.setRot(owner.getYRot(), owner.getXRot());
        this.moveTo(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d0 = Math.sqrt(x * x + y * y + z * z);
        if (d0 != 0.0D) {
            this.xPower = x / d0;
            this.yPower = y / d0;
            this.zPower = z / d0;
        }
    }

    public HomingWitherSkull(Level world, LivingEntity owner, double x, double y, double z, LivingEntity target) {
        this(world, owner, x, y, z);
        setTarget(target.getUUID());
        setAutoTarget(false);
    }

    public Optional<UUID> getTargetUUID() {
        return entityData.get(DATA_TARGET_UUID);
    }

    @Nullable
    public Entity getTarget() {
        Optional<UUID> o = getTargetUUID();
        if (o.isEmpty()) return null;
        UUID targetUUID = o.get();
        List<Entity> entities = level.getEntities(this, getBoundingBox().inflate(50.0), (e) -> e.getUUID() == targetUUID);
        if (entities.size() < 1) return null;
        return entities.get(0);
    }

    public void setTarget(UUID uuid) {
        entityData.set(DATA_TARGET_UUID, Optional.of(uuid));
    }

    public void setNoTarget() {
        entityData.set(DATA_TARGET_UUID, Optional.empty());
    }

    public boolean shouldAutoTarget() {
        return entityData.get(DATA_AUTO_TARGET);
    }

    public void setAutoTarget(boolean autoTarget) {
        entityData.set(DATA_AUTO_TARGET, autoTarget);
    }

    @Nullable
    public Entity findTarget() {
        List<Entity> entities = level.getEntities(this, getBoundingBox().inflate(50), targetSelector);
        if (entities.size() > 0) {
            float shortestDistance = Float.MAX_VALUE;
            Entity maxEntity = entities.get(0);
            for (Entity e : entities) {
                if (e.distanceTo(this) < shortestDistance && ((e instanceof Player player && !player.getAbilities().instabuild) || !(e instanceof Player))) {
                    shortestDistance = e.distanceTo(this);
                    maxEntity = e;
                }
            }
            return maxEntity;
        }
        return null; // No entities within range
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_TARGET_UUID, Optional.empty());
        entityData.define(DATA_AUTO_TARGET, true);
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level.isClientSide || this.level.hasChunkAt(this.blockPosition())) {

            speed *= 1.025;
            super.tick();
            if (this.shouldBurn()) {
                this.setSecondsOnFire(1);
            }

            HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f = this.getInertia();
            if (this.isInWater()) {
                for(int i = 0; i < 4; ++i) {
                    this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
                }

                f = 0.8F;
            }

            this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
            this.level.addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
            this.setPos(d0, d1, d2);
            if (getTargetUUID().isEmpty() || shouldAutoTarget()) {
                Entity e = findTarget();
                if (e != null) setTarget(e.getUUID());
                else setNoTarget();
            }

            Entity target = getTarget();
            
            if (target != null) {
                double x = target.getX() - getX();
                double y = (target.getY() + target.getEyeHeight()) - getY();
                double z = target.getZ() - getZ();
                Vec3 vec = new Vec3(x, y, z).normalize();
                setDeltaMovement(vec.multiply(speed, speed, speed));
            }
        } else {
            this.discard();
        }
    }

    @Override
    public void onHit(HitResult result) {
        Entity target = getTarget();
        if (target != null && distanceToSqr(target) <= 9.0) super.onHit(result);
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity target = getTarget();
        if (target != null && distanceToSqr(target) <= 9.0) super.onHitEntity(result);
    }
}
