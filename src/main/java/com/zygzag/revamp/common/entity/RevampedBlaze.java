package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RevampedBlaze extends Monster {
    public static final EntityDataAccessor<Boolean> DATA_IS_GUARD = SynchedEntityData.defineId(RevampedBlaze.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions threeLayers = new EntityDimensions(0.6f, 1.8f, false);
    private static final EntityDimensions twoLayers = new EntityDimensions(0.6f, 1.3f, false);
    private static final EntityDimensions oneLayer = new EntityDimensions(0.6f, 0.6f, false);
    private static final EntityDimensions head = new EntityDimensions(0.4f, 0.4f, false);
    private final BlazeRodEntity[] rods;
    private int randomNum;
    public List<Vec3> positions = new LinkedList<>();
    public static final int ticksBackToCachePositions = 8;

    public RevampedBlaze(EntityType<? extends RevampedBlaze> type, Level world) {
        this(type, world, (int) (Math.random() * 13));
    }

    public RevampedBlaze(EntityType<? extends RevampedBlaze> type, Level world, int numRods) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 12, true);
        rods = new BlazeRodEntity[12];
        for (int i = 0; i < ticksBackToCachePositions; i++) positions.add(position());
        if (!level.isClientSide) {
            this.randomNum = world.random.nextInt(16);
            for (int i = 0; i < numRods; i++) {
                rods[i] = new BlazeRodEntity(this, i, randomNum / 16f * Math.PI / 2);
            }
        }
    }

    public BlazeRodEntity getRod(int i) {
        return rods[i];
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        BlazeRodEntity[] arr = new BlazeRodEntity[GeneralUtil.count(rods, Objects::nonNull)];
        int c = 0;
        for (BlazeRodEntity rod : rods) {
            if (rod != null) {
                arr[c] = rod;
                c++;
            }
        }
        return arr;
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.MONSTER;
    }

    @Override
    protected AABB makeBoundingBox() {
        return getDimensions(Pose.STANDING).makeBoundingBox(position());
    }

    @Override
    public void tick() {
        this.yBodyRotO = yBodyRot;
        setOldPosAndRot();
        super.tick();
        this.dimensions = getDimensions(Pose.STANDING);
        this.eyeHeight = getEyeHeight(Pose.STANDING);
        refreshPositions();
        for (BlazeRodEntity rod : rods) {
            if (rod != null) rod.tick();
        }
    }

    public void refreshPositions() {
        positions.remove(ticksBackToCachePositions - 1);
        positions.add(0, position());
    }

    public Vec3 pastPosition(int ticksBack) {
        if (ticksBack > ticksBackToCachePositions) ticksBack = ticksBackToCachePositions;
        return positions.get(ticksBack - 1);
    }

    public int totalLayers() {
        return (int) Math.ceil(numRods() / 4f);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, numRods() | (randomNum << 4));
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.randomNum = packet.getData() >> 4;
        for (int i = 0; i < (packet.getData() & 0b1111); i++) {
            rods[i] = new BlazeRodEntity(this, i, randomNum / 16f * Math.PI / 2);
        }
        BlazeRodEntity[] parts = this.getSubEntities();

        for (int i = 0; i < parts.length; ++i) {
            if (parts[i] != null) parts[i].setId(i + packet.getId());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        for (BlazeRodEntity rod : rods) {
            if (rod != null) {
                CompoundTag rodTag = new CompoundTag();
                rodTag.putString("type", rod.type.toString());
                rodTag.putString("state", rod.state.toString());
                list.add(rodTag);
            }
        }
        tag.put("rods", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag list = tag.getList("rods", Tag.TAG_COMPOUND);
        int i = 0;
        for (Tag t : list) {
            if (t instanceof CompoundTag ct) {
                BlazeRodEntity rod = new BlazeRodEntity(this, i, randomNum / 16f * Math.PI / 2);
                rod.type = BlazeRodEntity.Type.valueOf(ct.getString("type"));
                rod.state = BlazeRodEntity.State.valueOf(ct.getString("state"));
                rods[i] = rod;
                i++;
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        int n = numRods();
        if (n > 8) return threeLayers;
        else if (n > 4) return twoLayers;
        else if (n > 0) return oneLayer;
        else return head;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return getEyeHeight(pose, getDimensions(pose));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 3).add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    public int getArmorValue() {
        return numRods() * 2;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_IS_GUARD, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new BlazeEvadeEntitiesGoal(this, (entity) -> !entity.getType().is(RevampTags.BLAZE_COMFORTABLE.get()) && (!(entity instanceof Player player) || !(player.getAbilities().instabuild || player.isSpectator()))));
        this.goalSelector.addGoal(4, new BlazeAttackGoal(this));
        this.goalSelector.addGoal(5, new BlazeWanderRandomGoal());
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
        return rods == null ? 0 : GeneralUtil.count(rods, Objects::nonNull);
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

    public State getState() {
        if (isOnGuard()) return State.GUARD;
        int n = numRods();
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
        State (int balls, boolean isEvasive) {
            this.balls = balls;
            this.isEvasive = isEvasive;
        }
    }

    public BlazeRodEntity[] getSubEntities() {
        return this.rods;
    }

    public int numFireballs() {
        return getState().balls;
    }

    public boolean isEvasive() {
        return getState().isEvasive;
    }

    class BlazeWanderRandomGoal extends Goal {
        @Override
        public void tick() {
            if (level.random.nextFloat() < 0.01) {
                Vec3 rand = GeneralUtil.randVectorNormalized(level.random).scale(8.0 * level.random.nextFloat());
                Vec3 newPos = position().add(rand);
                if (GeneralUtil.clearPathExists(position(), newPos, level, RevampedBlaze.this))
                    getMoveControl().setWantedPosition(newPos.x(), newPos.y(), newPos.z(), 1);
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            return true;
        }
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

                if ((state == State.NEUTRAL || state == State.HOSTILE) && !blaze.isEvasive()) {
                    if (Math.abs(distance - 10) > 4) { // if more than 4 blocks from 10 block sphere around player
                        Vec3 pos = blaze.position().subtract(target.position());
                        pos = pos.scale(10.0 / pos.length());
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
                } else if (distance < this.getFollowDistance() && flag) { // if target within follow distance and i have line of sight
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
                            double d4 = Math.sqrt(distance) * 0.5D;
                            if (!this.blaze.isSilent()) {
                                this.blaze.level.levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                            }

                            for (int i = 0; i < 1; ++i) {
                                SmallFireball smallfireball = new SmallFireball(this.blaze.level, this.blaze, d1 + this.blaze.getRandom().nextGaussian() * d4, d2, d3 + this.blaze.getRandom().nextGaussian() * d4);
                                smallfireball.setPos(smallfireball.getX(), this.blaze.getEyeY(), smallfireball.getZ());
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
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }

    public static class BlazeEvadeEntitiesGoal extends Goal {
        private Predicate<Entity> predicate;
        private RevampedBlaze blaze;

        public BlazeEvadeEntitiesGoal(RevampedBlaze blaze, Predicate<Entity> predicate) {
            this.blaze = blaze;
            this.predicate = predicate;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (blaze.isEvasive()) {
                Level world = blaze.level;
                List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, blaze.getBoundingBox().inflate(15.0), (entity) -> entity != blaze && predicate.test(entity));
                if (entities.size() > 0) {
                    Vec3 minDistEscapePos = entities.get(0).position();
                    for (LivingEntity e : entities) {
                        if (predicate.test(e) && e.distanceToSqr(blaze) < minDistEscapePos.distanceToSqr(blaze.position()))
                            minDistEscapePos = e.position();
                    }
                    Vec3 p = blaze.position().subtract(minDistEscapePos).scale(4.0).add(minDistEscapePos);
                    blaze.getMoveControl().setWantedPosition(p.x, p.y, p.z, 1.0);
                }
            }
        }
    }

    public double yBodyRot(float partialTick) {
        return Mth.rotLerp(partialTick, yBodyRotO, yBodyRot);
    }

    public double yHeadRot(float partialTick) {
        return Mth.rotLerp(partialTick, yHeadRotO, yHeadRot);
    }

    public double yRot(float partialTick) {
        return Mth.rotLerp(partialTick, yRot, yRotO);
    }

    // revamp: entity methods that I decided to add to all my entities (i would add them to the vanilla Entity class if i could)
    public Vec3 oldPos() {
        return new Vec3(xOld, yOld, zOld);
    }

    public Vec3 position(float partialTick) {
        return oldPos().lerp(position(), partialTick);
    }
}
