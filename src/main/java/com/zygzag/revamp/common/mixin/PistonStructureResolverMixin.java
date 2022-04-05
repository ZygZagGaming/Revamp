package com.zygzag.revamp.common.mixin;

import com.zygzag.revamp.common.block.ConditionalStickyBlock;
import com.zygzag.revamp.common.block.OsteumBlock;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private Direction pushDirection;

    @Shadow
    @Final
    private BlockPos pistonPos;

    @Shadow
    @Final
    private List<BlockPos> toPush;

    @Shadow
    @Final
    private List<BlockPos> toDestroy;

    @Inject(at = @At("HEAD"), method = "addBlockLine(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", cancellable = true)
    private void addBlockLine(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> callback) {
        BlockState blockstate = level.getBlockState(pos);
        if (level.isEmptyBlock(pos)) {
            callback.setReturnValue(true);
        } else if (!PistonBaseBlock.isPushable(blockstate, this.level, pos, this.pushDirection, false, dir)) {
            callback.setReturnValue(true);
        } else if (pos.equals(this.pistonPos)) {
            callback.setReturnValue(true);
        } else if (this.toPush.contains(pos)) {
            callback.setReturnValue(true);
        } else {
            int i = 1;
            if (i + this.toPush.size() > 12) {
                callback.setReturnValue(false);
            } else {
                BlockState oldState;
                while (blockstate.isStickyBlock()) {
                    BlockPos blockpos = pos.relative(this.pushDirection.getOpposite(), i);
                    oldState = blockstate;
                    blockstate = this.level.getBlockState(blockpos);
                    if (blockstate.isAir() || !canStick(oldState, blockstate, pos, blockpos, this.pushDirection.getOpposite()) || !PistonBaseBlock.isPushable(blockstate, this.level, blockpos, this.pushDirection, false, this.pushDirection.getOpposite()) || blockpos.equals(this.pistonPos)) {
                        break;
                    }

                    ++i;
                    if (i + this.toPush.size() > 12) {
                        callback.setReturnValue(false);
                        return;
                    }
                }

                int l = 0;

                for(int i1 = i - 1; i1 >= 0; --i1) {
                    this.toPush.add(pos.relative(this.pushDirection.getOpposite(), i1));
                    ++l;
                }

                int j1 = 1;

                while(true) {
                    BlockPos blockpos1 = pos.relative(this.pushDirection, j1);
                    BlockPos lastPos = blockpos1.relative(pushDirection.getOpposite());
                    BlockState lastState = level.getBlockState(lastPos);
                    int j = this.toPush.indexOf(blockpos1);
                    if (j > -1) {
                        this.reorderListAtCollision(l, j);

                        for(int k = 0; k <= j + l; ++k) {
                            BlockPos blockpos2 = this.toPush.get(k);
                            if (this.level.getBlockState(blockpos2).isStickyBlock() && !this.addBranchingBlocks(blockpos2)) {
                                callback.setReturnValue(false);
                                break;
                            }
                        }

                        callback.setReturnValue(true);
                        break;
                    }

                    blockstate = this.level.getBlockState(blockpos1);
                    if (blockstate.isAir()) {
                        callback.setReturnValue(true);
                        break;
                    }

                    if (!PistonBaseBlock.isPushable(blockstate, this.level, blockpos1, this.pushDirection, true, this.pushDirection) || blockpos1.equals(this.pistonPos)) {
                        callback.setReturnValue(false);
                        break;
                    }
                    if (blockstate.getPistonPushReaction() == PushReaction.DESTROY || (lastState.is(Registry.OSTEUM.get()) && lastState.getValue(OsteumBlock.getProperty(pushDirection)) == OsteumBlock.OsteumSideState.SHARPENED) && toPush.contains(lastPos)) {
                        this.toDestroy.add(blockpos1);
                        callback.setReturnValue(true);
                        break;
                    }

                    if (this.toPush.size() >= 12) {
                        callback.setReturnValue(false);
                        break;
                    }

                    this.toPush.add(blockpos1);
                    ++l;
                    ++j1;
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "addBranchingBlocks(Lnet/minecraft/core/BlockPos;)Z", cancellable = true)
    private void addBranchingBlocks(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        BlockState blockstate = this.level.getBlockState(pos);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.pushDirection.getAxis()) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate1 = this.level.getBlockState(blockpos);
                if (canStick(blockstate, blockstate1, pos, blockpos, direction) && !this.addBlockLine(blockpos, direction)) {
                    callback.setReturnValue(false);
                    return;
                }
            }
        }
        callback.setReturnValue(true);
    }

    @Shadow
    private void reorderListAtCollision(int a, int b) { }

    @Shadow
    private boolean addBranchingBlocks(BlockPos pos) {
        throw new IllegalStateException("Mixins failed");
    }

    private boolean canStick(BlockState a, BlockState b, BlockPos aPos, BlockPos bPos, Direction dir) {
        boolean x = true, y = true, z;
        System.out.println("trying to stick " + a.getBlock() + " and " + b.getBlock());
        if (a.getBlock() instanceof ConditionalStickyBlock c) x = c.canStickTo(a, b, aPos, bPos, dir);
        if (b.getBlock() instanceof ConditionalStickyBlock c) y = c.canStickTo(b, a, bPos, aPos, dir.getOpposite());
        z = x && y;
        if (a.getBlock() instanceof ConditionalStickyBlock && b.getBlock() instanceof ConditionalStickyBlock) z = x || y;
        return a.canStickTo(b) && z;
    }

    @Shadow
    private boolean addBlockLine(BlockPos pos, Direction dir) {
        throw new IllegalStateException("Mixin failed");
    }
}
