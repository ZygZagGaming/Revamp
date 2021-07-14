package com.zygzag.revamp.common.entity;
import com.zygzag.revamp.common.Registry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DrifterEntity extends MobEntity {
    public final int age; //0=larva, 1=baby, 2=adult
    public DrifterEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        age = getRandom().nextInt(3);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.1F).add(Attributes.ARMOR, 0.0D);
    }
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new DrifterEntity.MoveRandomGoal(this));
    }
    public void aiStep() {
        super.aiStep();
    }

    public boolean canBreatheUnderwater() {
        return true;
    }
    public boolean isNoGravity() {
        return true;
    }
    public boolean isPersistenceRequired() {
        return true;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return Registry.DRIFTER_EGG_HIT.get();
    }
    @Nullable
    protected SoundEvent getDeathSound() {
        return Registry.DRIFTER_EGG_POP.get();
    }

    class MoveRandomGoal extends Goal {
        private final DrifterEntity entity;
        private float timeUntilDirectionSwitch;
        private Vector3d direction;
        private float speed = 0.0005f;
        public float rotx;
        public float roty;
        public MoveRandomGoal(DrifterEntity drifter) {
            this.entity = drifter;
            direction = new Vector3d(0,0,0);
            rotx = (this.entity.getRandom().nextFloat() * 6) - 3;
            roty = (this.entity.getRandom().nextFloat() * 6) - 3;
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            timeUntilDirectionSwitch--;
            if (timeUntilDirectionSwitch < 1){
                timeUntilDirectionSwitch = (entity.getRandom().nextFloat() * 200);
                float f = this.entity.getRandom().nextFloat() * ((float)Math.PI * 2F);
                float f1 = MathHelper.cos(f) * speed;
                float f2 = (-speed) + this.entity.getRandom().nextFloat() * speed * 2;
                float f3 = MathHelper.sin(f) * speed;
                direction = new Vector3d(f1, f2, f3);
            }

            entity.turn(rotx,roty);
            Vector3d movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x + direction.x, movement.y + direction.y, movement.z + direction.z);
        }
    }
}
