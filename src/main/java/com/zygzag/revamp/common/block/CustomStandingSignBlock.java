package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.block.entity.CustomSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CustomStandingSignBlock extends StandingSignBlock {
    public CustomStandingSignBlock(Properties prop, WoodType type) {
        super(prop, type);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomSignBlockEntity(pos, state);
    }
}
