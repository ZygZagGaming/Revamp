package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.block.entity.CustomSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CustomWallSignBlock extends WallSignBlock {
    public CustomWallSignBlock(Properties prop, WoodType type) {
        super(prop, type);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomSignBlockEntity(pos, state);
    }
}
