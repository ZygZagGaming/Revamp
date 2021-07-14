package com.zygzag.revamp.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

public class DrifterEggEntity extends  MobEntity {
    public DrifterEggEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        setNoGravity(true);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 4).add(Attributes.MOVEMENT_SPEED, 0.1F).add(Attributes.ARMOR, 0.0D);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }
}
