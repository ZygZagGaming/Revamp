package com.zygzag.revamp.common.mixin;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@SuppressWarnings({"ConstantConditions", "unused"})
@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack stack, CallbackInfo callback) {
        player.awardStat(Stats.BLOCK_MINED.get((Block) (Object) this));
        player.causeFoodExhaustion(0.005F);
        if (stack.getItem() != Registry.WITHER_SKULL_SOCKETED_IRIDIUM_PICKAXE.get()) dropResources(state, world, pos, be, player, stack);
        callback.cancel();
    }

    @Shadow
    public static void dropResources(BlockState p_49882_, Level p_49883_, BlockPos p_49884_, @Nullable BlockEntity p_49885_, Entity p_49886_, ItemStack p_49887_) { }
}
