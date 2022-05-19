package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RevampedBlaze extends Monster {
    public static final EntityDataAccessor<Boolean> DATA_IS_GUARD = SynchedEntityData.defineId(RevampedBlaze.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Rods> DATA_RODS = SynchedEntityData.defineId(RevampedBlaze.class, (EntityDataSerializer<Rods>) Registry.EntityDataSerializerRegistry.ROD_SERIALIZER_ENTRY.get().getSerializer());
    private static final EntityDimensions threeLayers = new EntityDimensions(0.6f, 1.8f, false);
    private static final EntityDimensions twoLayers = new EntityDimensions(0.6f, 1.3f, false);
    private static final EntityDimensions oneLayer = new EntityDimensions(0.6f, 0.6f, false);
    private static final EntityDimensions head = new EntityDimensions(0.4f, 0.4f, false);

    @Override
    protected AABB makeBoundingBox() {
        return getDimensions(Pose.STANDING).makeBoundingBox(position());
    }

    public Rods getRods() {
        return entityData.get(DATA_RODS);
    }

    @Override
    public void tick() {
        super.tick();
        this.dimensions = getDimensions(Pose.STANDING);
        this.eyeHeight = getEyeHeight(Pose.STANDING);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        int n = numRods();
        if (n > 8) return threeLayers;
        else if (n > 4) return twoLayers;
        else if (n > 0) return oneLayer;
        else return head;
    }

    public RevampedBlaze(EntityType<? extends RevampedBlaze> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 12, true);
        if (!level.isClientSide) {
            Rods j = new Rods(this);
            int k = (int) (Math.random() * 13);
            for (int i = 0; i < k; i++) {
                j.rods.add(Rod.REGULAR);
            }
            entityData.set(DATA_RODS, j);
        }
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return getEyeHeight(pose, getDimensions(pose));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 0.6).add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    public int getArmorValue() {
        return numRods() * 2;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_IS_GUARD, false);
        entityData.define(DATA_RODS, new Rods(this));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(4, new BlazeAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, (k) -> getState() == State.HOSTILE || getState() == State.GUARD));
    }

    public boolean isOnGuard() {
        return entityData.get(DATA_IS_GUARD);
    }

    public boolean causeFallDamage(float a, float b, DamageSource source) {
        return false;
    }

    public int numRods() {
        return getRods().numRods();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_32235_) {
        return SoundEvents.BLAZE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    public float getBrightness() {
        return 1.0F;
    }

    boolean k = false;

    public void aiStep() {
        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            k = !k;

            if (k) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.aiStep();
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        ListTag k = nbt.getList("rods", Tag.TAG_STRING);
        List<Rod> l = new ArrayList<>();
        for (Tag tag : k) {
            Rod rod = Rod.valueOf(tag.getAsString());
            l.add(rod);
        }
        setRods(new Rods(l, getUUID()));
    }

    public void setRods(Rods rods) {
        entityData.set(DATA_RODS, rods);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag listTag = new ListTag();
        Rods rods = getRods();
        for (Rod rod : rods.rods) {
            listTag.add(StringTag.valueOf(rod.toString()));
        }
        tag.put("rods", listTag);
    }

    public static class Rods {
        public List<Rod> rods;
        public UUID blazeUUID;
        public Rods(List<Rod> rods, UUID blazeUUID) {
            this.rods = rods;
            this.blazeUUID = blazeUUID;
        }

        public Rods(RevampedBlaze blaze) {
            this(new ArrayList<>(), blaze.getUUID());
        }

        public int numRods() {
            return rods.size();
        }
    }

    public enum Rod {
        REGULAR,
        NONE,
        SHIELD,
        DART
    }

    public State getState() {
        if (isOnGuard()) return State.GUARD;
        int n = getRods().numRods();
        if (n <= 2) return State.PASSIVE;
        else if (n <= 4) return State.NEUTRAL_EVASIVE;
        else if (n <= 8) return State.NEUTRAL;
        else return State.HOSTILE;
    }

    public enum State {
        PASSIVE(0, true), // try to escape player
        NEUTRAL_EVASIVE(1, true), // try to escape player, if attacked will fire a fireball and run
        NEUTRAL(2, false), // try to escape player, will stand its ground if attacked
        HOSTILE(3, false), // normal blaze behavior
        GUARD(3, false); // tries to remain in the same place while attacking on sight
        public int balls;
        public boolean isEvasive;
        State(int balls, boolean isEvasive) {
            this.balls = balls;
            this.isEvasive = isEvasive;
        }
    }

    public int numFireballs() {
        return getState().balls;
    }

    public boolean isEvasive() {
        return getState().isEvasive;
    }

    static class BlazeAttackGoal extends Goal {
        private final RevampedBlaze blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public BlazeAttackGoal(RevampedBlaze blaze) {
            this.blaze = blaze;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.blaze.getTarget();
            return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity target = this.blaze.getTarget();
            if (target != null) {
                boolean flag = this.blaze.getSensing().hasLineOfSight(target);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double distance = this.blaze.distanceTo(target);
                State state = blaze.getState();

                if (state == State.NEUTRAL || state == State.HOSTILE) {
                    if (Math.abs(distance - 10) > 4) { // if more than 4 blocks from 10 block sphere around player
                        Vec3 pos = blaze.position().subtract(target.position());
                        pos = pos.scale(20.0 / pos.length());
                        pos = pos.add(target.position());
                        blaze.getMoveControl().setWantedPosition(pos.x(), pos.y(), pos.z(), 1);
                    }
                }

                if (distance < 4.0D) {
                    if (!flag || blaze.getState() != State.GUARD) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.blaze.doHurtTarget(target);
                    }

                    this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
                } else if (distance < this.getFollowDistance() * this.getFollowDistance() && flag) { // if target within follow distance and i have line of sight
                    double d1 = target.getX() - this.blaze.getX();
                    double d2 = target.getY(0.5D) - this.blaze.getY(0.5D);
                    double d3 = target.getZ() - this.blaze.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60; // if starting attack wait 3 seconds for fireballs
                        } else if (this.attackStep <= blaze.numFireballs() + 1) {
                            this.attackTime = 6; // wait 6 ticks between each fireball
                        } else {
                            this.attackTime = 100; // wait 5 seconds before doing anything else
                            this.attackStep = 0;
                        }

                        if (this.attackStep > 1) { // if should fire fireball
                            double d4 = Math.sqrt(Math.sqrt(distance)) * 0.5D;
                            if (!this.blaze.isSilent()) {
                                this.blaze.level.levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                            }

                            for (int i = 0; i < 1; ++i) {
                                SmallFireball smallfireball = new SmallFireball(this.blaze.level, this.blaze, d1 + this.blaze.getRandom().nextGaussian() * d4, d2, d3 + this.blaze.getRandom().nextGaussian() * d4);
                                smallfireball.setPos(smallfireball.getX(), this.blaze.getY(0.5D) + 0.5D, smallfireball.getZ());
                                this.blaze.level.addFreshEntity(smallfireball);
                            }
                        }


                    }

                    this.blaze.getLookControl().setLookAt(target, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
                }

                super.tick();
            }

            if (blaze.isEvasive()) {
                Level world = blaze.level;
                List<Entity> entities = world.getEntities(blaze, blaze.getBoundingBox().inflate(15.0));
                if (entities.size() > 0) {
                    Vec3 minDistEscapePos = entities.get(0).position();
                    for (Entity e : entities) {
                        if (e.distanceToSqr(blaze) < minDistEscapePos.distanceToSqr(blaze.position()))
                            minDistEscapePos = e.position();
                    }
                    Vec3 p = blaze.position().subtract(minDistEscapePos).scale(4.0).add(minDistEscapePos);
                    blaze.getMoveControl().setWantedPosition(p.x, p.y, p.z, 1.0);
                }
            }
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
