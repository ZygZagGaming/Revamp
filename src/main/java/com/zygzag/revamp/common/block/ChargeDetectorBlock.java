package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.block.entity.ChargeDetectorBlockEntity;
import com.zygzag.revamp.common.registry.BlockEntityTypeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChargeDetectorBlock extends DiodeBlock implements EntityBlock {
    public ChargeDetectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getDelay(BlockState state) {
        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction == state.getValue(FACING);
    }

    @Override
    protected boolean shouldTurnOn(Level world, BlockPos pos, BlockState state) {
        return getOutputSignal(world, pos, state) > 0;
    }

    @Override
    protected int getOutputSignal(BlockGetter world, BlockPos pos, BlockState state) {
        if (world instanceof Level level) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChargeDetectorBlockEntity chargeDetectorBe) {
                return chargeDetectorBe.output;
            }
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargeDetectorBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide || type != BlockEntityTypeRegistry.CHARGE_DETECTOR_BLOCK_ENTITY.get() ? null : helper(type, BlockEntityTypeRegistry.CHARGE_DETECTOR_BLOCK_ENTITY.get(), ChargeDetectorBlockEntity::serverTick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> helper(BlockEntityType<T> t, BlockEntityType<E> e, BlockEntityTicker<? super E> ticker) {
        return t == e ? (BlockEntityTicker<T>) ticker : null;
    }

    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        return state.getValue(FACING) == dir ? this.getOutputSignal(world, pos, state) : 0;
    }
}
