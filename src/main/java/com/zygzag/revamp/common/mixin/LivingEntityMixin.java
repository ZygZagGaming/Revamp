package com.zygzag.revamp.common.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Shadow public float xxa = 0;
    @Shadow public float zza = 0;
}
