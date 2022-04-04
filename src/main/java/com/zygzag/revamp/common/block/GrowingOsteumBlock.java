package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GrowingOsteumBlock extends Block {
    public static final EnumProperty<Direction> DIRECTION = EnumProperty.create("direction", Direction.class);
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 1, 5);

    public GrowingOsteumBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(DIRECTION, Direction.DOWN).setValue(STAGE, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION, STAGE);
    }

    public BlockState getStateForDirection(Direction direction) {
        return defaultBlockState().setValue(DIRECTION, direction);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rng) {
        if (rng.nextDouble() < 0.125) {
            if (state.getValue(STAGE) != 5) world.setBlockAndUpdate(pos, state.setValue(STAGE, state.getValue(STAGE) + 1));
            else world.setBlockAndUpdate(pos, ((OsteumBlock) Registry.OSTEUM.get()).getStateForAxis(state.getValue(DIRECTION).getAxis()));
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState state2 = world.getBlockState(pos.relative(state.getValue(DIRECTION).getOpposite()));
        return state2.is(Registry.OSTEUM.get()) && state2.getValue(OsteumBlock.getProperty(state.getValue(DIRECTION).getAxis()));
    }
}
