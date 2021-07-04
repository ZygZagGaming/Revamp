package com.zygzag.revamp.entity;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.function.Predicate;

public class AbominationWitherEntity extends WitherEntity {

    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (p_213797_0_) -> p_213797_0_.getMobType() != CreatureAttribute.UNDEAD && p_213797_0_.attackable();

    public AbominationWitherEntity(EntityType<? extends WitherEntity> type, World world) {
        super(type, world);
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

}
