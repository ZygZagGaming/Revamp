package com.zygzag.revamp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface ConditionalStickyBlock {
    boolean canStickTo(BlockState state, BlockState other, BlockPos pos, BlockPos otherPos, Direction dir);
}
