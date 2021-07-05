package com.zygzag.revamp.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.EnumSet;
import java.util.function.Predicate;

public class AbominationWitherEntity extends WitherEntity {

    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (p_213797_0_) -> p_213797_0_.getMobType() != CreatureAttribute.UNDEAD && p_213797_0_.attackable();
    private int destroyBlocksTick;

    public AbominationWitherEntity(EntityType<? extends WitherEntity> type, World world) {
        super(type, world);
        this.makeInvulnerable();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AbominationWitherEntity.DoNothingGoal());
        /*this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MobEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));*/
    }

    public class DoNothingGoal extends Goal {
        public DoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return AbominationWitherEntity.this.getInvulnerableTicks() > 0;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float amount) { }

    @Override
    protected void customServerAiStep() {
        if (this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0 && ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                int i1 = MathHelper.floor(this.getY());
                int l1 = MathHelper.floor(this.getX());
                int i2 = MathHelper.floor(this.getZ());
                boolean flag = false;

                for(int k2 = -1; k2 <= 1; ++k2) {
                    for(int l2 = -1; l2 <= 1; ++l2) {
                        for(int j = 0; j <= 3; ++j) {
                            int i3 = l1 + k2;
                            int k = i1 + j;
                            int l = i2 + l2;
                            BlockPos blockpos = new BlockPos(i3, k, l);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            if (blockstate.canEntityDestroy(this.level, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
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

    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source != DamageSource.DROWN && !(source.getEntity() instanceof WitherEntity)) {
            if (this.getInvulnerableTicks() > 0 && source != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (this.isPowered()) {
                    Entity entity = source.getDirectEntity();
                    if (entity instanceof AbstractArrowEntity) {
                        return false;
                    }
                }

                Entity entity1 = source.getEntity();
                if (!(entity1 instanceof PlayerEntity) && entity1 instanceof LivingEntity && ((LivingEntity) entity1).getMobType() == this.getMobType()) {
                    return false;
                } else {
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    return super.hurt(source, amount);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void makeInvulnerable() {
        this.setInvulnerableTicks(500);
        this.setHealth(this.getMaxHealth() / 5.0F);
    }
}
