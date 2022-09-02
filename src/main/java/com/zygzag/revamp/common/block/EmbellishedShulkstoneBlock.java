package com.zygzag.revamp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EmbellishedShulkstoneBlock extends RotatedPillarBlock {
    public EmbellishedShulkstoneBlock(Properties prop) {
        super(prop);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }
}
