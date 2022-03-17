package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.entity.goal.boss.RevampGoalSelector;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmpoweredWither extends WitherBoss {
    // region fields and constructors
    public static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (mob) -> mob.getMobType() != MobType.UNDEAD && mob.attackable();
    private int attackCooldown = 0;
    private int noGravTime = 0;
    private EmpoweredWitherHeadPart leftHead = new EmpoweredWitherHeadPart(this, "left_head", 0.85f, 0.85f, 1.2f, 1.3f, 2.3125f, 0.3125f);
    private EmpoweredWitherHeadPart rightHead = new EmpoweredWitherHeadPart(this, "right_head", 0.85f, 0.85f, 1.2f, -1.3f, 2.3125f, 0.3125f);
    private EmpoweredWitherHeadPart[] subEntities = { leftHead, rightHead };

    public static final DamageSource SLAM = new DamageSource("slam").damageHelmet();

    public EmpoweredWither(EntityType<? extends EmpoweredWither> type, Level world) {
        super(type, world);
    }
    // endregion

    // region methods
    @Override
    public void tick() {
        super.tick();
        if (attackCooldown > 0) attackCooldown--;
        if (noGravTime > 0) noGravTime--;
    }

    public void tickParts() {
        for (EmpoweredWitherHeadPart part : subEntities) {
            part.tickPos();
        }
    }

    @Override
    public boolean isNoGravity() {
        return noGravTime > 0;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Nullable
    @Override
    public PartEntity<?>[] getParts() {
        return subEntities;
    }

    public void setNoGravity(int noGravTime) {
        this.noGravTime = noGravTime;
    }

    public boolean hurt(EmpoweredWitherHeadPart part, DamageSource source, float amt) {
        return super.hurt(source, amt);
    }

    public EmpoweredWitherHeadPart[] getSubEntities() {
        return this.subEntities;
    }

    public void recreateFromPacket(ClientboundAddMobPacket packet) {
        super.recreateFromPacket(packet);
        EmpoweredWitherHeadPart[] parts = this.getSubEntities();

        for(int i = 0; i < parts.length; ++i) {
            parts[i].setId(i + packet.getId());
        }

    }
    // endregion

    // region boiler code to override default behavior
    @Override
    public void aiStep() {
        tickParts();
        mobAiStep();
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
        if (!this.level.isClientSide && this.getAlternativeTarget(0) > 0) {
            Entity entity = this.level.getEntity(this.getAlternativeTarget(0));
            if (entity != null) {
                double d0 = vec3.y;
                if (this.getY() < entity.getY() || !this.isPowered() && this.getY() < entity.getY() + 5.0D) {
                    d0 = Math.max(0.0D, d0);
                    d0 = d0 + (0.3D - d0 * (double)0.6F);
                }

                vec3 = new Vec3(vec3.x, d0, vec3.z);
                Vec3 vec31 = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
                if (vec31.horizontalDistanceSqr() > 9.0D) {
                    Vec3 vec32 = vec31.normalize();
                    vec3 = vec3.add(vec32.x * 0.3D - vec3.x * 0.6D, 0.0D, vec32.z * 0.3D - vec3.z * 0.6D);
                }
            }
        }

        this.setDeltaMovement(vec3);

        boolean flag = this.isPowered();

        for (int l = 0; l < 3; ++l) {
            double d8 = this.getHeadX(l);
            double d10 = this.getHeadY(l);
            double d2 = this.getHeadZ(l);
            this.level.addParticle(ParticleTypes.SMOKE, d8 + this.random.nextGaussian() * (double)0.3F, d10 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
            if (flag && this.level.random.nextInt(4) == 0) {
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d8 + this.random.nextGaussian() * (double)0.3F, d10 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.7F, 0.7F, 0.5D);
            }
        }

        if (this.getInvulnerableTicks() > 0) {
            for(int i1 = 0; i1 < 3; ++i1) {
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), 0.7F, 0.7F, 0.9F);
            }
        }

    }

    @Override
    protected void customServerAiStep() {
        if (this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                int j1 = Mth.floor(this.getY());
                int i2 = Mth.floor(this.getX());
                int j2 = Mth.floor(this.getZ());
                boolean flag = false;

                for(int j = -1; j <= 1; ++j) {
                    for(int k2 = -1; k2 <= 1; ++k2) {
                        for(int k = 0; k <= 3; ++k) {
                            int l2 = i2 + j;
                            int l = j1 + k;
                            int i1 = j2 + k2;
                            BlockPos blockpos = new BlockPos(l2, l, i1);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            if (blockstate.canEntityDestroy(this.level, blockpos, this) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                flag = this.level.destroyBlock(blockpos, true, this) || flag;
                            }
                        }
                    }
                }

                if (flag) {
                    this.level.levelEvent(null, 1022, this.blockPosition(), 0);
                }
            }
        }

        if (this.tickCount % 20 == 0) {
            this.heal(1.0F);
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void mobAiStep() {
        LivingEntity target = getTarget();
        if (target != null) {
            lookAt(target, 0.0f, 0.0f);
        }
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d6 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)d6 / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.getYRot(), this.getXRot());
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float)((double)this.yHeadRot + Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }

        Vec3 vec3 = this.getDeltaMovement();
        double d1 = vec3.x;
        double d3 = vec3.y;
        double d5 = vec3.z;
        if (Math.abs(vec3.x) < 0.003D) {
            d1 = 0.0D;
        }

        if (Math.abs(vec3.y) < 0.003D) {
            d3 = 0.0D;
        }

        if (Math.abs(vec3.z) < 0.003D) {
            d5 = 0.0D;
        }

        this.setDeltaMovement(d1, d3, d5);
        this.level.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        } else if (this.isEffectiveAi()) {
            this.level.getProfiler().push("newAi");
            this.serverAiStep();
            this.level.getProfiler().pop();
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("jump");
        if (this.jumping && this.isAffectedByFluids()) {
            double d7;
            if (this.isInLava()) {
                d7 = this.getFluidHeight(FluidTags.LAVA);
            } else {
                d7 = this.getFluidHeight(FluidTags.WATER);
            }

            boolean flag1 = this.isInWater() && d7 > 0.0D;
            double d8 = this.getFluidJumpThreshold();
            if (!flag1 || this.onGround && !(d7 > d8)) {
                if (!this.isInLava() || this.onGround && !(d7 > d8)) {
                    if ((this.onGround || flag1 && d7 <= d8)) {
                        this.jumpFromGround();
                    }
                } else {
                    this.jumpInLiquid(FluidTags.LAVA);
                }
            } else {
                this.jumpInLiquid(FluidTags.WATER);
            }
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("travel");
        this.xxa *= 0.98F;
        this.zza *= 0.98F;
        AABB aabb = this.getBoundingBox();
        this.travel(new Vec3(this.xxa, this.yya, this.zza));
        this.level.getProfiler().pop();
        this.level.getProfiler().push("freezing");
        boolean flag = this.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
        if (!this.level.isClientSide && !this.isDeadOrDying()) {
            int i = this.getTicksFrozen();
            if (this.isInPowderSnow && this.canFreeze()) {
                this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), i + 1));
            } else {
                this.setTicksFrozen(Math.max(0, i - 2));
            }
        }

        this.removeFrost();
        this.tryAddFrost();
        if (!this.level.isClientSide && this.tickCount % 40 == 0 && this.isFullyFrozen() && this.canFreeze()) {
            int j = flag ? 5 : 1;
            this.hurt(DamageSource.FREEZE, (float)j);
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.checkAutoSpinAttack(aabb, this.getBoundingBox());
        }

        this.pushEntities();
        this.level.getProfiler().pop();
        if (!this.level.isClientSide && this.isSensitiveToWater() && this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0F);
        }
    }
    // endregion

    // region goals
    @Override
    protected void registerGoals() {
        goalSelector = new RevampGoalSelector(level.getProfilerSupplier()); // replace goalSelector with custom
        //goalSelector.addGoal(0, new EmpoweredWitherDoNothingGoal());
        goalSelector.addGoal(0, new DoAttackGoal(
                new SlamGoal(3),
                new ShootAttackGoal(5),
                new ShootVolleyAttackGoal(3),
                new ShootQuadVolleyAttackGoal(2)
        ));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    static class EmpoweredWitherDoNothingGoal extends Goal {
        public EmpoweredWitherDoNothingGoal() {
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }
    }

    abstract class AttackGoal extends Goal {
        private int attackTime, totalAttackTime, weight;
        private boolean isCanceled = false;

        public AttackGoal(int totalAttackTime, int weight) {
            setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK));
            this.totalAttackTime = totalAttackTime;
            this.weight = weight;
        }

        @Override
        public void start() {
            super.start();
            isCanceled = false;
            attackTime = totalAttackTime;
        }

        public boolean canUse() {
            return attackCooldown <= 0;
        }

        public boolean canContinueToUse() {
            boolean flag = this.attackTime >= 0 && !isCanceled;
            if (flag) end();
            return flag;
        }

        public void cancel() {
            isCanceled = true;
        }

        private void end() {
            attackCooldown += totalAttackTime;
        }

        public int getWeight() {
            return weight;
        }

        public int getAttackTime() {
            return attackTime;
        }

        public void decrementAttackTime() {
            attackTime--;
        }

        public int getTotalAttackTime() {
            return totalAttackTime;
        }
    }

    class DoAttackGoal extends Goal  {
        private AttackGoal[] attacks;
        private @Nullable AttackGoal currentGoal;

        public DoAttackGoal(AttackGoal... attacks) {
            this.attacks = attacks;
        }

        @Override
        public boolean canUse() {
            return currentGoal == null || !currentGoal.canContinueToUse();
        }

        @Override
        public boolean canContinueToUse() {
            return currentGoal != null && currentGoal.canContinueToUse();
        }

        @Override
        public void stop() {
            super.stop();
            if (currentGoal != null) currentGoal.stop();
        }

        @Override
        public void tick() {
            super.tick();
            if (currentGoal != null) currentGoal.tick();
        }

        @Override
        public void start() {
            if (currentGoal != null) currentGoal.cancel();
            ArrayList<AttackGoal> al = new ArrayList<>();
            for (AttackGoal goal : attacks) {
                if (goal.canUse()) {
                    al.add(goal);
                }
            }
            AttackGoal[] availableGoals = new AttackGoal[al.size()];
            availableGoals = al.toArray(availableGoals);
            int totalWeight = 0;
            int[] availableWeights = new int[availableGoals.length];
            for (int i = 0; i < availableGoals.length; i++) {
                availableWeights[i] = availableGoals[i].getWeight();
                totalWeight += availableWeights[i];
            }
            AttackGoal goal = GeneralUtil.weightedRandom(availableGoals, availableWeights, totalWeight);
            if (goal != null) {
                goal.start();
                currentGoal = goal;
            }
        }
    }

    class SlamGoal extends AttackGoal {
        public SlamGoal(int weight) {
            super(100, weight);
        }

        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target == null) return false;
            Vec3 targetPos = target.position();
            Vec3 pos = position();
            Vec3 diff = targetPos.subtract(pos);
            return diff.normalize().y <= -0.75;
        }

        @Override
        public void start() {
            super.start();
        }

        public void tick() {
            decrementAttackTime();
            if (level.getBlockState(blockPosition().above(3)).isAir() && getAttackTime() > 90) {
                moveTo(position().add(new Vec3(0, 0.5, 0)));
            } else if (level.getBlockState(blockPosition().below()).isAir() && getAttackTime() <= 90) {
                if (!isNoGravity()) setNoGravity(90);
                moveTo(position().add(new Vec3(0, -2, 0)));
            } else {
                cancel();
                List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(7d, 2d, 7d));
                for (LivingEntity mob : mobs) if (mob != EmpoweredWither.this) {
                    mob.hurt(SLAM, 12f);
                    mob.knockback((float)3 * 0.5F, Mth.sin(getYRot() * ((float)Math.PI / 180F)), -Mth.cos(getYRot() * ((float)Math.PI / 180F)));
                }
            }
        }
    }

    class ShootAttackGoal extends AttackGoal {
        public ShootAttackGoal(int weight) {
            super(40, weight);
        }

        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target == null) return false;
            Vec3 targetPos = target.position();
            Vec3 pos = position();
            return pos.distanceTo(targetPos) <= 40;
        }

        @Override
        public void start() {
            super.start();
        }

        public void tick() {
            decrementAttackTime();
            if (getAttackTime() == 20) {
                LivingEntity target = getTarget();
                if (target != null) {
                    Vec3 targetPosLocal = getTarget().position().subtract(position());
                    HomingWitherSkull skull = shootHomingSkull(target);
                    level.addFreshEntity(skull);
                    if (!isSilent()) {
                        level.levelEvent(null, 1024, blockPosition(), 0);
                    }
                }
            }
        }
    }

    class ShootVolleyAttackGoal extends AttackGoal {
        @Nullable
        private static AttackDirection tickToDirection(int tick) {
            switch (tick) {
                case 20 -> {
                    return AttackDirection.FORWARD;
                }
                case 30 -> {
                    return AttackDirection.LEFT;
                }
                case 40 -> {
                    return AttackDirection.RIGHT;
                }
                default -> {
                    return null;
                }
            }
        }

        public ShootVolleyAttackGoal(int weight) {
            super(80, weight);
        }

        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target == null) {
                return false;
            }
            Vec3 targetPos = target.position();
            Vec3 pos = position();
            return pos.distanceTo(targetPos) <= 40;
        }

        @Override
        public void start() {
            super.start();
        }

        public void tick() {
            decrementAttackTime();
            AttackDirection dir = tickToDirection(getAttackTime());
            if (dir != null) { // fires at 20, 30, and 40 ticks into the attack
                LivingEntity target = getTarget();
                if (target != null) {
                    Vec3 targetPosLocal = getTarget().position().subtract(position());
                    // HomingWitherSkull skull = new HomingWitherSkull(level, EmpoweredWither.this, targetPosLocal.x, targetPosLocal.y, targetPosLocal.z);
                    HomingWitherSkull skull = shootHomingSkull(target, dir);
                    level.addFreshEntity(skull);
                    if (!isSilent()) {
                        level.levelEvent(null, 1024, blockPosition(), 0);
                    }
                }
            }
        }
    }

    class ShootQuadVolleyAttackGoal extends AttackGoal {
        @Nullable
        private static AttackDirection tickToDirection(int tick) {
            switch (tick) {
                case 20, 36 -> {
                    return AttackDirection.LEFT;
                }
                case 28, 44 -> {
                    return AttackDirection.RIGHT;
                }
                default -> {
                    return null;
                }
            }
        }

        public ShootQuadVolleyAttackGoal(int weight) {
            super(80, weight);
        }

        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target == null) return false;
            Vec3 targetPos = target.position();
            Vec3 pos = position();
            return pos.distanceTo(targetPos) <= 40;
        }

        @Override
        public void start() {
            super.start();
        }

        public void tick() {
            decrementAttackTime();
            AttackDirection dir = tickToDirection(getAttackTime());
            if (dir != null) { // fires at 20, 28, 36, and 44 ticks into the attack
                LivingEntity target = getTarget();
                if (target != null) {
                    Vec3 targetPosLocal = getTarget().position().subtract(position());
                    // HomingWitherSkull skull = new HomingWitherSkull(level, EmpoweredWither.this, targetPosLocal.x, targetPosLocal.y, targetPosLocal.z);
                    HomingWitherSkull skull = shootHomingSkull(target, dir);
                    level.addFreshEntity(skull);
                    if (!isSilent()) {
                        level.levelEvent(null, 1024, blockPosition(), 0);
                    }
                }
            }
        }
    }

    private HomingWitherSkull shootHomingSkull(LivingEntity target) {
        return shootHomingSkull(target, AttackDirection.FORWARD);
    }

    private HomingWitherSkull shootHomingSkull(LivingEntity target, AttackDirection direction) {
        float yRot = getYRot() + direction.n;
        HomingWitherSkull skull = new HomingWitherSkull(level, EmpoweredWither.this, Mth.sin(yRot), 0, -Mth.cos(yRot), target);
        level.addFreshEntity(skull);
        return skull;
    }

    enum AttackDirection {
        FORWARD(0.0),
        LEFT(Math.PI / 2),
        RIGHT(3 * Math.PI / 2);

        public float n;

        AttackDirection(double n) {
            this.n = (float) (n + ((3 * Math.PI) / 4));
        }
    }
    // endregion
}
