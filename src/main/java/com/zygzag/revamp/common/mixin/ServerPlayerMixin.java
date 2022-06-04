package com.zygzag.revamp.common.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin extends LivingEntityMixin {
    @Inject(at = @At("TAIL"), method = "setPlayerInput(FFZZ)V")
    public void setPlayerInput(float xxa, float zza, boolean jumping, boolean shifting, CallbackInfo ci) {
        this.xxa = xxa;
        this.zza = zza;
    }
}
